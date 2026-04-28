package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.entity.Notification;
import ru.urfu.online_school_project_ai.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.urfu.online_school_project_ai.dto.AdminNotificationCreateDto;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Уведомление не найдено"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещен: это не ваше уведомление");
        }

        notification.setIs_read(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification createAdminNotification(AdminNotificationCreateDto dto) {
        if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
            List<User> selectedUsers = userRepository.findAllById(dto.getUserIds());
            if (selectedUsers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователи не найдены");
            }

            List<Notification> notifications = new ArrayList<>();
            for (User user : selectedUsers) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setText(dto.getText());
                // Если тип не указан, ставим SYSTEM по умолчанию
                notification.setType(dto.getType() != null && !dto.getType().isBlank() ? dto.getType() : "SYSTEM");
                notification.setIs_read(false);
                notifications.add(notification);
            }
            notificationRepository.saveAll(notifications);

            Notification summary = new Notification();
            summary.setText("Успешно разослано " + selectedUsers.size() + " пользователям");
            return summary;
        } else {
            // Рассылка всем пользователям
            List<User> allUsers = userRepository.findAll();
            List<Notification> notifications = new ArrayList<>();
            for (User user : allUsers) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setText(dto.getText());
                // Если тип не указан, ставим SYSTEM по умолчанию
                notification.setType(dto.getType() != null && !dto.getType().isBlank() ? dto.getType() : "SYSTEM");
                notification.setIs_read(false);
                notifications.add(notification);
            }
            notificationRepository.saveAll(notifications);
            // Возвращаем просто первое уведомление для примера или можно поменять возвращаемый тип,
            // но для простоты вернем пустой объект Notification или null
            Notification summary = new Notification();
            summary.setText("Разослано пользователям: " + allUsers.size());
            return summary;
        }
    }
}
