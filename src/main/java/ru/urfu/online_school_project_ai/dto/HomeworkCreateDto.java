package ru.urfu.online_school_project_ai.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record HomeworkCreateDto(
        UUID lessonId,
        String textAssignment,
        ZonedDateTime deadline,
        List<Long> taskIds
) {
}

