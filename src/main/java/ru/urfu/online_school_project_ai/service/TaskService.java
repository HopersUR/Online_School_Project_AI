package ru.urfu.online_school_project_ai.service;

import ru.urfu.online_school_project_ai.dto.TaskResponseDto;

import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getTasksByTopic(Long topicId);
    List<TaskResponseDto> getTasks(Long topicId, String difficulty);
    TaskResponseDto getTaskById(Long id);
}
