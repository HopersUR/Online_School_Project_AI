package ru.urfu.online_school_project_ai.dto;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        Long topicId,
        Integer taskNumber,
        String difficulty,
        String fileName
) {
}
