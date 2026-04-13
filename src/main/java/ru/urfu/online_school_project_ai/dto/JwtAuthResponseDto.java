package ru.urfu.online_school_project_ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthResponseDto {
    private String accessToken;

    public JwtAuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

}

