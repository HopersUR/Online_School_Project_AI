package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urfu.online_school_project_ai.dto.TaskResponseDto;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public List<TaskResponseDto> getTasks(Long topicId, String difficulty) {
        List<Task> tasks;
        if (topicId != null && difficulty != null) {
            tasks = taskRepository.findByTopicsIdAndDifficulty(topicId, difficulty);
        } else if (topicId != null) {
            tasks = taskRepository.findByTopicsId(topicId);
        } else if (difficulty != null) {
            tasks = taskRepository.findByDifficulty(difficulty);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public List<TaskResponseDto> getTasksByTopic(Long topicId) {
        return taskRepository.findByTopicsId(topicId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TaskResponseDto mapToDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTopics().getId(),
                task.getTask_number(),
                task.getDifficulty()
        );
    }
}
