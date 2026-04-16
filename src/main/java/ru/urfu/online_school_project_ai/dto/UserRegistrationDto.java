package ru.urfu.online_school_project_ai.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;


public record UserRegistrationDto (
        @Email
        String email,
        @Pattern(regexp="^(?=.*\\d)(?=.*[!#$%^&*()_@])(?=.*[a-z])(?=.*[A-Z]).*$", message = "Пароль должен содержать хотя бы одну цифру, один специальный символ (!#$%^&*()_@), а также одну строчную и заглавную букву")
        String password,
        @NotBlank(message = "Номер телефона обязателен для заполнения")
        String phone
    )
{}
