package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urfu.online_school_project_ai.dto.TaskResponseDto;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.repository.TaskRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import ru.urfu.online_school_project_ai.dto.SubmitAnswerResponseDto;
import ru.urfu.online_school_project_ai.entity.Solution;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.SolutionRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SolutionRepository solutionRepository;

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

    public TaskResponseDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    public List<TaskResponseDto> getTasksByTopic(Long topicId) {
        return taskRepository.findByTopicsId(topicId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ResponseEntity<Resource> downloadTaskFile(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        if (task.getFilePath() == null) {
            throw new RuntimeException("У задачи нет файла");
        }

        try {
            Path filePath = Paths.get(task.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                        .filename(task.getFileName(), StandardCharsets.UTF_8)
                        .build();

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                        .body(resource);
            } else {
                throw new RuntimeException("Файл не найден");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка при скачивании файла", e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при определении типа файла", e);
        }
    }

    private TaskResponseDto mapToDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTopics().getId(),
                task.getTask_number(),
                task.getDifficulty(),
                task.getFileName()
        );
    }

    public SubmitAnswerResponseDto submitTaskAnswer(Long taskId, String answer, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        boolean isCorrect = false;
        if (task.getAnswer() != null && answer != null) {
            isCorrect = task.getAnswer().trim().equalsIgnoreCase(answer.trim());
        }

        Solution solution = new Solution();
        solution.setUser(user);
        solution.setTask(task);
        solution.setCode(answer); // Сохраняем текстовый ответ в поле code
        if (isCorrect) {
            solution.setStatus("SUCCESS");
        } else {
            solution.setStatus("WRONG_ANSWER");
        }

        solutionRepository.save(solution);

        return SubmitAnswerResponseDto.builder()
                .isCorrect(isCorrect)
                .message(isCorrect ? "Ответ верный!" : "Неверный ответ")
                .build();
    }
}
