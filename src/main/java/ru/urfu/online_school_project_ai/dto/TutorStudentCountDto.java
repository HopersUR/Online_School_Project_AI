package ru.urfu.online_school_project_ai.dto;

import java.util.UUID;

public record TutorStudentCountDto(
        String tutorName,
        int studentCount
) {}

