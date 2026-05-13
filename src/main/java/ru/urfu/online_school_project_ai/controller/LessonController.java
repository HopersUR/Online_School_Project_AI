package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.urfu.online_school_project_ai.dto.LessonCreateDto;
import ru.urfu.online_school_project_ai.dto.LessonResponseDto;
import ru.urfu.online_school_project_ai.service.LessonService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Управление расписанием и занятиями")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    @Operation(summary = "Создать занятие")
    public ResponseEntity<LessonResponseDto> createLesson(
            Authentication authentication,
            @RequestBody LessonCreateDto dto) {
        return ResponseEntity.ok(lessonService.createLesson(authentication.getName(), dto));
    }

    @GetMapping("/tutor")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    @Operation(summary = "Получить расписание репетитора")
    public ResponseEntity<List<LessonResponseDto>> getTutorSchedule(Authentication authentication) {
        return ResponseEntity.ok(lessonService.getTutorSchedule(authentication.getName()));
    }

    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Получить расписание ученика")
    public ResponseEntity<List<LessonResponseDto>> getStudentSchedule(Authentication authentication) {
        return ResponseEntity.ok(lessonService.getStudentSchedule(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить конкретное занятие по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<LessonResponseDto> getLessonById(Authentication authentication, @PathVariable UUID id) {
        return ResponseEntity.ok(lessonService.getLessonById(authentication.getName(), id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    @Operation(summary = "Удалить занятие по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteLesson(Authentication authentication, @PathVariable UUID id) {
        lessonService.deleteLesson(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/link")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    @Operation(summary = "Добавить или изменить ссылку на занятие", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<LessonResponseDto> updateLessonLink(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestParam String link) {
        return ResponseEntity.ok(lessonService.updateLessonLink(authentication.getName(), id, link));
    }

    @PatchMapping("/{id}/video-link")
    @PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
    @Operation(summary = "Добавить ссылку на видео", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<LessonResponseDto> addVideoLink(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestParam String videoLink) {
        return ResponseEntity.ok(lessonService.addVideoLink(authentication.getName(), id, videoLink));
    }
}
