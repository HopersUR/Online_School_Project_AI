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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final TaskRepository taskRepository;
    private final TopicRepository topicRepository;

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

        return taskRepository.save(task);
    }
}
