package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.urfu.online_school_project_ai.dto.TaskResponseDto;
import ru.urfu.online_school_project_ai.dto.SubmitAnswerResponseDto;
import ru.urfu.online_school_project_ai.service.TaskService;

import java.security.Principal;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи")
@SecurityRequirement(name = "JWT")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получить список заданий с фильтрацией")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getTasks(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(taskService.getTasks(topicId, difficulty));
    }

    @Operation(summary = "Получить задание по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Скачать файл, прикрепленный к задаче")
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadTaskFile(@PathVariable Long id) {
        return taskService.downloadTaskFile(id);
    }

    @Operation(summary = "Отправить текстовый ответ на задачу")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}/submit-answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubmitAnswerResponseDto> submitTaskAnswer(
            @PathVariable Long id,
            @RequestParam("answer") String answer,
            Principal principal) {
        return ResponseEntity.ok(taskService.submitTaskAnswer(id, answer, principal.getName()));
    }
}
