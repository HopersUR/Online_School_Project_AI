package ru.urfu.online_school_project_ai.dto;

import java.util.List;
import java.util.UUID;

public record TutorNotificationCreateDto(
        List<UUID> studentIds,
        String text,
        String type
) {
}

