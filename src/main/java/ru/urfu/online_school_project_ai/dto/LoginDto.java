package ru.urfu.online_school_project_ai.dto;

import lombok.Getter;
import lombok.Setter;

public record LoginDto (
    String email,
    String password
    )
{}

