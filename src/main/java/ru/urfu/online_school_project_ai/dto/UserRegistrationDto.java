package ru.urfu.online_school_project_ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String email;
    private String password;
    private String confirmPassword;

}
