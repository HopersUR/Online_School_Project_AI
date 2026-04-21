package ru.urfu.online_school_project_ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.online_school_project_ai.dto.TaskResponseDto;
import ru.urfu.online_school_project_ai.dto.TopicDto;
import ru.urfu.online_school_project_ai.repository.TopicRepository;
import ru.urfu.online_school_project_ai.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Темы")
public class TopicController {

    private final TopicRepository topicRepository;
    private final TaskService taskService;

    @Operation(summary = "Получение списка тем")
    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        return ResponseEntity.ok(topicRepository.findAll().stream()
                .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Задания по теме с опциональным фильтром по сложности")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> getTasksByTopic(
            @PathVariable Long id,
            @Parameter(description = "Сложность задачи (Легкая, Средняя, Сложная, ГРОБ)")
            @RequestParam(required = false) String difficulty) {

        return ResponseEntity.ok(taskService.getTasks(id, difficulty));
    }
}
