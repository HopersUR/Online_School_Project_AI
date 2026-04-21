package ru.urfu.online_school_project_ai.dto;

public record CodeExecutionResponseDto(
        String output,
        String error,
        boolean success,
        boolean isAnswerCorrect
) {
}
