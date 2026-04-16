package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.online_school_project_ai.service.UserService;

import java.security.Principal;
import ru.urfu.online_school_project_ai.dto.UserProfileDto;
import ru.urfu.online_school_project_ai.dto.UpdateProfileDto;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserService userService;

    // Доступно всем авторизованным пользователям
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    // Обновление профиля для авторизованного пользователя
    @PatchMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            Principal principal,
            @RequestBody UpdateProfileDto dto) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), dto));
    }

}
