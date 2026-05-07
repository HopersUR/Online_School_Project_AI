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
import ru.urfu.online_school_project_ai.repository.AiAnalysisRepository;
import ru.urfu.online_school_project_ai.entity.AiAnalysis;
import ru.urfu.online_school_project_ai.entity.enums.AiErrorType;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final SolutionRepository solutionRepository;
    private final ErrorRepository errorRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final OpenRouterService openRouterService;
    private final AiAnalysisRepository aiAnalysisRepository;

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
                return new CodeExecutionResponseDto(null, "Execution timed out (Time Limit Exceeded)", false, false, "Время выполнения истекло");
            }

            // Читаем стандартный вывод (результат print)
            String output = readStream(process.getInputStream());

            // Читаем стандартный вывод ошибок (исключения Python)
            String error = readStream(process.getErrorStream());

            boolean success = process.exitValue() == 0;
            boolean isAnswerCorrect = false;
            String aiRecommendation = null;

            // Сохранение решения в базу данных
            if (taskId != null) {
                User user = userRepository.findByEmail(username);
                Task task = taskRepository.findById(taskId).orElse(null);

                if (user != null && task != null) {

                    // Вызываем AI для анализа кода, если он был выполнен, либо даже если с ошибкой
                    String expectedAnswer = task.getAnswer() != null ? String.valueOf(task.getAnswer()) : null;
                    aiRecommendation = openRouterService.analyzeStudentCode(code, task.getDescription() != null ? task.getDescription() : "Код ученика", expectedAnswer, output, error);

                    // Мы больше не удаляем старое решение, чтобы сохранять историю всех решений и ИИ анализов к ним.
                    // solutionRepository.findByUserAndTask(user, task).ifPresent(solutionRepository::delete);

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
                    solution.setAiRecommendation(aiRecommendation);

                    solutionRepository.save(solution);

                    parseAndSaveAiAnalysis(aiRecommendation, solution, user, task);
                }
            } else {
                aiRecommendation = openRouterService.analyzeStudentCode(code, "Общая задача", null, output, error);
            }

            return new CodeExecutionResponseDto(output, error, success, isAnswerCorrect, aiRecommendation);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeExecutionResponseDto(null, "Internal Server Error: " + e.getMessage(), false, false, null);
        }
    }

    private void parseAndSaveAiAnalysis(String aiOutput, Solution solution, User user, Task task) {
        if (aiOutput == null || aiOutput.isBlank()) return;

        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setSolution(solution);
        aiAnalysis.setStudent(user);
        aiAnalysis.setTask(task);
        aiAnalysis.setResult_text(aiOutput);

        // Парсинг оценки (например, "Оценка: 85")
        try {
            java.util.regex.Pattern scorePattern = java.util.regex.Pattern.compile("(?i)Оценка.*?(\\d{1,3})");
            java.util.regex.Matcher scoreMatcher = scorePattern.matcher(aiOutput);
            if (scoreMatcher.find()) {
                aiAnalysis.setScore(Integer.parseInt(scoreMatcher.group(1)));
            }
        } catch (Exception ignored) {}

        // Используем простые регулярные выражения для извлечения секций
        String errorTypesStr = extractSection(aiOutput, "Типы ошибок", "Эффективность", "Сильные стороны", "Слабые стороны", "Рекомендации", "Комментарий", "Комментарии");
        List<AiErrorType> errorTypes = new ArrayList<>();
        if (errorTypesStr != null && !errorTypesStr.isBlank()) {
            String[] tokens = errorTypesStr.split(",");
            for (String token : tokens) {
                try {
                    String cleanToken = token.replaceAll("[^A-Za-z_]", "").trim().toUpperCase();
                    if (!cleanToken.isEmpty()) {
                        errorTypes.add(AiErrorType.valueOf(cleanToken));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        aiAnalysis.setErrorTypes(errorTypes);

        aiAnalysis.setEfficiency(extractSection(aiOutput, "Эффективность", "Сильные стороны", "Слабые стороны", "Рекомендации"));
        aiAnalysis.setStrengths(extractSection(aiOutput, "Сильные стороны", "Слабые стороны", "Рекомендации"));
        aiAnalysis.setWeaknesses(extractSection(aiOutput, "Слабые стороны", "Рекомендации", "Комментарий", "Комментарии"));
        aiAnalysis.setRecommendations(extractSection(aiOutput, "Рекомендации", "Комментарий", "Комментарии"));

        aiAnalysisRepository.save(aiAnalysis);
    }

    private String extractSection(String text, String startMarker, String... endMarkers) {
        try {
            int startIndex = text.toLowerCase().indexOf(startMarker.toLowerCase());
            if (startIndex == -1) return null;

            startIndex += startMarker.length();
            if (startIndex < text.length() && text.charAt(startIndex) == ':') {
                startIndex++;
            }

            int endIndex = text.length();
            for (String endMarker : endMarkers) {
                int index = text.toLowerCase().indexOf(endMarker.toLowerCase(), startIndex);
                if (index != -1 && index < endIndex) {
                    endIndex = index;
                }
            }

            return text.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return null;
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
