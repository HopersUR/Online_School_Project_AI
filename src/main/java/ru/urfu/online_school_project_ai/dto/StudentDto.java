package ru.urfu.online_school_project_ai.dto;

import java.util.Date;
import java.util.UUID;

public record StudentDto(
        UUID id,
        String name,
        String avatar,
        String level,
        Date dateExam
) {}

