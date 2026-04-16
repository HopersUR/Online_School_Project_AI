package ru.urfu.online_school_project_ai.dto;

import ru.urfu.online_school_project_ai.entity.enums.Role;
import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String email,
        String phone,
        Role role,
        String name,
        String avatar
) {}
