package ru.urfu.online_school_project_ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.online_school_project_ai.dto.CodeExecutionResponseDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import ru.urfu.online_school_project_ai.entity.Error;
import ru.urfu.online_school_project_ai.entity.Solution;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.repository.ErrorRepository;
import ru.urfu.online_school_project_ai.repository.SolutionRepository;
import ru.urfu.online_school_project_ai.repository.TaskRepository;
import ru.urfu.online_school_project_ai.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final SolutionRepository solutionRepository;
    private final ErrorRepository errorRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;


    public CodeExecutionResponseDto executePythonCode(Long taskId, MultipartFile file, String username) throws IOException {
        String code = new String(file.getBytes(), StandardCharsets.UTF_8);

        try {
            // Создаем процесс Docker: запускаем контейнер python:3.10
            // Флаги: --rm (удалить контейнер после выполнения), -i (интерактивный режим для передачи кода через stdin)
            // Мы передаем ограничение по памяти (--memory=128m) и без сети (--network none) для безопасности.
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "--memory=128m",
                    "--cpus=0.5",
                    "--network", "none",
                    "--pids-limit=64",
                    "-i",
                    "python:3.10",
                    "python", "-"
            );

            Process process = pb.start();

            // Записываем код в stdin процесса Docker
            try (OutputStream os = process.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                writer.write(code);
                writer.flush();
            }

            // Ждем завершения с таймаутом (например, 15 секунд)
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return new CodeExecutionResponseDto(null, "Execution timed out (Time Limit Exceeded)", false, false);
            }

            // Читаем стандартный вывод (результат print)
            String output = readStream(process.getInputStream());

            // Читаем стандартный вывод ошибок (исключения Python)
            String error = readStream(process.getErrorStream());

            boolean success = process.exitValue() == 0;
            boolean isAnswerCorrect = false;

            // Сохранение решения в базу данных
            if (taskId != null) {
                User user = userRepository.findByEmail(username);
                Task task = taskRepository.findById(taskId).orElse(null);

                if (user != null && task != null) {

                    // Удаляем старое решение
                    solutionRepository.findByUserAndTask(user, task).ifPresent(solutionRepository::delete);

                    isAnswerCorrect = success && output.trim().equals(String.valueOf(task.getAnswer()).trim());

                    Solution solution = new Solution();
                    solution.setCode(code);
                    solution.setUser(user);
                    solution.setTask(task);

                    if (!success) {
                        solution.setStatus("RUNTIME_ERROR");
                    } else if (isAnswerCorrect) {
                        solution.setStatus("SUCCESS");
                    } else {
                        solution.setStatus("WRONG_ANSWER");
                    }

                    List<Error> savedErrors = new ArrayList<>();
                    if (!success && !error.isEmpty()) {
                        Error errorEntity = new Error();
                        errorEntity.setName("Execution Error");
                        errorEntity.setDescription(error.length() > 255 ? error.substring(0, 255) : error);
                        errorRepository.save(errorEntity);
                        savedErrors.add(errorEntity);
                    }
                    solution.setErrors(savedErrors);

                    solutionRepository.save(solution);
                }
            }

            return new CodeExecutionResponseDto(output, error, success, isAnswerCorrect);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeExecutionResponseDto(null, "Internal Server Error: " + e.getMessage(), false, false);
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString().trim();
    }
}
