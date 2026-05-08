package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.entity.Tutor;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.entity.Student;
import ru.urfu.online_school_project_ai.entity.enums.Role;
import ru.urfu.online_school_project_ai.repository.StudentRepository;
import ru.urfu.online_school_project_ai.repository.TutorRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.entity.Topic;
import ru.urfu.online_school_project_ai.repository.TaskRepository;
import ru.urfu.online_school_project_ai.repository.TopicRepository;
import ru.urfu.online_school_project_ai.dto.TaskCreateDto;
import ru.urfu.online_school_project_ai.dto.AdminDashboardDto;
import ru.urfu.online_school_project_ai.dto.TutorStudentCountDto;
import ru.urfu.online_school_project_ai.repository.LessonRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final TaskRepository taskRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;

    public AdminDashboardDto getDashboardStatistics() {
        long studentsCount = studentRepository.count();
        long tutorsCount = tutorRepository.count();

        List<TutorStudentCountDto> tutorsStudentCounts = tutorRepository.findAll().stream()
                .map(tutor -> new TutorStudentCountDto(
                        tutor.getName() != null ? tutor.getName() : "Не указано",
                        tutor.getStudents() == null ? 0 : tutor.getStudents().size()
                ))
                .collect(Collectors.toList());

        ZonedDateTime weekAgo = ZonedDateTime.now().minusDays(7);
        long lessonsThisWeekCount = lessonRepository.countByCreatedAtAfter(weekAgo);

        return new AdminDashboardDto(studentsCount, tutorsCount, tutorsStudentCounts, lessonsThisWeekCount);
    }

    @Transactional
    public void changeUserRole(UUID userId, Role newRole) {
        if (newRole == Role.ADMIN) {
            throw new IllegalArgumentException("Назначение роли ADMIN запрещено в целях безопасности");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

        user.setRole(newRole);
        user = userRepository.save(user);

        // Если назначаем роль репетитора, нужно убедиться, что у него есть связанный профиль Tutor
        if (newRole == Role.TUTOR) {
            if (!tutorRepository.existsById(userId)) {
                Tutor tutor = new Tutor();
                tutor.setUser(user);
                tutorRepository.save(tutor);
            }
            // Удаляем профиль студента, если он существует
            if (studentRepository.existsById(userId)) {
                studentRepository.deleteById(userId);

            }
        }
        else if (newRole == Role.STUDENT) {
            if (!studentRepository.existsById(userId)) {
                Student student = new Student();
                student.setUser(user);
                studentRepository.save(student);
            }
            // Удаляем профиль репетитора, если он существует
            if (tutorRepository.existsById(userId)) {
                tutorRepository.deleteById(userId);
            }
        }
    }

    @Transactional
    public void assignTutorToStudent(UUID studentId, UUID tutorId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Ученик с ID " + studentId + " не найден"));

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Репетитор с ID " + tutorId + " не найден"));

        // Убеждаемся, что список инициализирован
        if (tutor.getStudents() == null) {
            tutor.setStudents(new java.util.ArrayList<>());
        }

        if (!tutor.getStudents().contains(student)) {
            tutor.getStudents().add(student);
            tutorRepository.save(tutor);
        }
    }

    @Transactional
    public Task addTask(TaskCreateDto dto) {
        Topic topic = topicRepository.findById(dto.topicId())
                .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + dto.topicId() + " не найдена"));

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setTopics(topic);
        task.setTask_number(dto.taskNumber());
        task.setDifficulty(dto.difficulty());
        task.setAnswer(dto.answer());
        task.setImage_url(dto.imageUrl());

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, ru.urfu.online_school_project_ai.dto.TaskUpdateDto dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + taskId + " не найдена"));

        updateIfNotNull(dto.title(), task::setTitle);
        updateIfNotNull(dto.description(), task::setDescription);
        updateIfNotNull(dto.taskNumber(), task::setTask_number);
        updateIfNotNull(dto.difficulty(), task::setDifficulty);
        updateIfNotNull(dto.answer(), task::setAnswer);
        updateIfNotNull(dto.imageUrl(), task::setImage_url);

        if (dto.topicId() != null) {
            Topic topic = topicRepository.findById(dto.topicId())
                    .orElseThrow(() -> new IllegalArgumentException("Тема с ID " + dto.topicId() + " не найдена"));
            task.setTopics(topic);
        }

        return taskRepository.save(task);
    }

    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Задача с ID " + taskId + " не найдена");
        }
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void uploadTaskFile(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + taskId + " не найдена"));

        try {
            String uploadDir = "uploads/tasks/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            task.setFileName(file.getOriginalFilename());
            task.setFilePath(filePath.toString());
            taskRepository.save(task);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }
}
