package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.online_school_project_ai.dto.HomeworkCreateDto;
import ru.urfu.online_school_project_ai.dto.HomeworkResponseDto;
import ru.urfu.online_school_project_ai.service.HomeworkService;

@RestController
@RequestMapping("/api/homeworks")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class HomeworkController {

    private final HomeworkService homeworkService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<HomeworkResponseDto> createHomework(@RequestBody HomeworkCreateDto dto) {
        HomeworkResponseDto response = homeworkService.createHomework(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('TUTOR', 'STUDENT')")
    public ResponseEntity<HomeworkResponseDto> getHomeworkByLessonId(@PathVariable java.util.UUID lessonId) {
        HomeworkResponseDto response = homeworkService.getHomeworkByLessonId(lessonId);
        return ResponseEntity.ok(response);
    }
}
