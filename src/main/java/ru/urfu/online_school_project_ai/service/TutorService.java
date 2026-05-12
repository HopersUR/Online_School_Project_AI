package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.StudentDto;
import ru.urfu.online_school_project_ai.dto.TutorNotificationCreateDto;
import ru.urfu.online_school_project_ai.entity.Notification;
import ru.urfu.online_school_project_ai.entity.Student;
import ru.urfu.online_school_project_ai.entity.Tutor;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.NotificationRepository;
import ru.urfu.online_school_project_ai.repository.StudentRepository;
import ru.urfu.online_school_project_ai.repository.TutorRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsForTutor(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Tutor tutor = tutorRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Профиль репетитора не найден"));

        if (tutor.getStudents() == null) {
            return List.of();
        }

        return tutor.getStudents().stream()
                .map(s -> new StudentDto(
                        s.getId(),
                        s.getName(),
                        s.getAvatar(),
                        s.getLevel(),
                        s.getDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createNotificationForStudents(String email, TutorNotificationCreateDto dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Tutor tutor = tutorRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Профиль репетитора не найден"));

        List<Student> tutorStudents = tutor.getStudents();
        if (tutorStudents == null || tutorStudents.isEmpty()) {
            throw new IllegalArgumentException("У вас нет учеников");
        }

        List<User> targetUsers = new ArrayList<>();
        if (dto.studentIds() == null || dto.studentIds().isEmpty()) {
            // Отправляем всем ученикам
            for (Student s : tutorStudents) {
                targetUsers.add(s.getUser());
            }
        } else {
            // Отправляем выбранным ученикам
            for (UUID stId : dto.studentIds()) {
                boolean isMyStudent = tutorStudents.stream().anyMatch(s -> s.getId().equals(stId));
                if (!isMyStudent) {
                    throw new IllegalArgumentException("Ученик с ID " + stId + " не является вашим учеником");
                }
                Student st = studentRepository.findById(stId)
                        .orElseThrow(() -> new IllegalArgumentException("Ученик не найден"));
                targetUsers.add(st.getUser());
            }
        }

        List<Notification> notifications = new ArrayList<>();
        for (User u : targetUsers) {
            Notification notification = new Notification();
            notification.setUser(u);
            notification.setText(dto.text());
            notification.setCreatedAt(ZonedDateTime.now());
            notification.setType(dto.type() != null && !dto.type().isBlank() ? dto.type() : "MESSAGE");
            notification.setIs_read(false);
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
    }
}
