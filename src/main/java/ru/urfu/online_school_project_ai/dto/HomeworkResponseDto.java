package ru.urfu.online_school_project_ai.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record HomeworkResponseDto(
        UUID id,
        UUID lessonId,
        boolean isCompleted,
        ZonedDateTime deadline,
        String textAssignment,
        List<Long> taskIds
) {
}

