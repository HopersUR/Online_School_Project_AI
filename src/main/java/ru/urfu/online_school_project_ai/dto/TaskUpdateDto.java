package ru.urfu.online_school_project_ai.dto;

public record TaskUpdateDto(
        String title,
        String description,
        Long topicId,
        Integer taskNumber,
        String difficulty,
        String answer,
        String imageUrl
) {}
