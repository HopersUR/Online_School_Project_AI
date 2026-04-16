package ru.urfu.online_school_project_ai.dto;

import jakarta.validation.constraints.NotNull;
import ru.urfu.online_school_project_ai.entity.enums.Role;

public record ChangeRoleDto(
        Role role
) {}

