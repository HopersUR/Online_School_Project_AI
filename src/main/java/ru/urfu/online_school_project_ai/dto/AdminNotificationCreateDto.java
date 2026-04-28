package ru.urfu.online_school_project_ai.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AdminNotificationCreateDto {
    private String text;
    private String type; // Тип уведомления (задается администратором)
    private List<UUID> userIds; // Список ID пользователей (если пустой или null - рассылка всем)
}
