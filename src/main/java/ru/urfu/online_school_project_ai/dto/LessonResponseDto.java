package ru.urfu.online_school_project_ai.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LessonResponseDto(
    UUID id,
    Integer studentLessonNumber,
    String title,
    String description,
    UUID tutorId,
    UUID studentId,
    String meetingLink,
    String videoLessonLink,
    ZonedDateTime scheduledAt,
    ZonedDateTime createdAt
) {}
