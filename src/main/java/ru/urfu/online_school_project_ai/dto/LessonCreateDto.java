package ru.urfu.online_school_project_ai.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LessonCreateDto(
    String title,
    String description,
    UUID tutorId,
    UUID studentId,
    ZonedDateTime scheduledAt
) {}
