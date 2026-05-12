package ru.urfu.online_school_project_ai.dto;

import java.util.List;
import java.util.UUID;


public record AdminNotificationCreateDto(
        String text,
        String type, // Тип уведомления (задается администратором)
        List<UUID> userIds // Список ID пользователей (если пустой или null - рассылка всем)

)
{}