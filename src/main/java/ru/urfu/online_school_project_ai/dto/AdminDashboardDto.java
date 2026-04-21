package ru.urfu.online_school_project_ai.dto;

import java.util.List;

public record AdminDashboardDto(
        long studentsCount,
        long tutorsCount,
        List<TutorStudentCountDto> tutorsStudentCounts,
        long lessonsThisWeekCount
) {
}

