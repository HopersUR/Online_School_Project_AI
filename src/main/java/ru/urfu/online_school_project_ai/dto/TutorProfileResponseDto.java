package ru.urfu.online_school_project_ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TutorProfileResponseDto(
        UUID id,
        String name,
        String avatar,
        String email,
        String experience,
        BigDecimal rating
) {
}

