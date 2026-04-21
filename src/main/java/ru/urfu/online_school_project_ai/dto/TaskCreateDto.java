package ru.urfu.online_school_project_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateDto(

        String title,

        @NotBlank
        String description,

        @NotNull
        Long topicId,

        @NotNull
        Integer taskNumber,

        @NotBlank
        String difficulty,

        @NotBlank
        String answer,

        String imageUrl
) {
}
