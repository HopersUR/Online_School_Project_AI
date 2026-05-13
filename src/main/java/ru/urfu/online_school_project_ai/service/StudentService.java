package ru.urfu.online_school_project_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.online_school_project_ai.dto.StudentStatisticsDto;
import ru.urfu.online_school_project_ai.dto.TutorProfileResponseDto;
import ru.urfu.online_school_project_ai.dto.TopicStatisticsDto;
import ru.urfu.online_school_project_ai.entity.AiAnalysis;
import ru.urfu.online_school_project_ai.entity.Solution;
import ru.urfu.online_school_project_ai.entity.Student;
import ru.urfu.online_school_project_ai.entity.Tutor;
import ru.urfu.online_school_project_ai.entity.User;
import ru.urfu.online_school_project_ai.entity.Topic;
import ru.urfu.online_school_project_ai.entity.enums.AiErrorType;
import ru.urfu.online_school_project_ai.repository.AiAnalysisRepository;
import ru.urfu.online_school_project_ai.repository.SolutionRepository;
import ru.urfu.online_school_project_ai.repository.StudentRepository;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final SolutionRepository solutionRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public StudentStatisticsDto getStudentDashboard(User studentUser) {
        List<Solution> userSolutions = solutionRepository.findByUser(studentUser);

        long totalSolutions = userSolutions.size();

        long uniqueAttemptedTasks = userSolutions.stream()
                .filter(s -> s.getTask() != null)
                .map(s -> s.getTask().getId())
                .distinct()
                .count();

        long successfullySolved = userSolutions.stream()
                .filter(s -> "SUCCESS".equals(s.getStatus()) && s.getTask() != null)
                .map(s -> s.getTask().getId())
                .distinct()
                .count();

        // Считаем статистику по темам
        Map<Topic, List<Solution>> solutionsByTopic = userSolutions.stream()
                .filter(s -> s.getTask() != null && s.getTask().getTopics() != null)
                .collect(Collectors.groupingBy(s -> s.getTask().getTopics()));

        List<TopicStatisticsDto> topicStatsList = new ArrayList<>();
        for (Map.Entry<Topic, List<Solution>> entry : solutionsByTopic.entrySet()) {
            Topic topic = entry.getKey();
            List<Solution> topicSolutions = entry.getValue();

            long tAttempted = topicSolutions.stream()
                    .map(s -> s.getTask().getId())
                    .distinct()
                    .count();

            long tSolved = topicSolutions.stream()
                    .filter(s -> "SUCCESS".equals(s.getStatus()))
                    .map(s -> s.getTask().getId())
                    .distinct()
                    .count();

            long tSubmitted = topicSolutions.size();

            topicStatsList.add(new TopicStatisticsDto(
                    topic.getName(),
                    tAttempted,
                    tSolved,
                    tSubmitted
            ));
        }

        Double avgScore = aiAnalysisRepository.getAverageScoreByStudent(studentUser);

        List<AiAnalysis> analyses = aiAnalysisRepository.findBySolutionUser(studentUser);
        Map<AiErrorType, Long> errorMatrix = analyses.stream()
                .flatMap(a -> a.getErrorTypes().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return new StudentStatisticsDto(
                uniqueAttemptedTasks,
                successfullySolved,
                totalSolutions,
                avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0,
                errorMatrix,
                topicStatsList
        );
    }

    public List<TutorProfileResponseDto> getMyTutors(User currentUser) {
        Student student = studentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден"));

        if (student.getTutors() == null || student.getTutors().isEmpty()) {
            return List.of();
        }

        return student.getTutors().stream()
                .map(tutor -> new TutorProfileResponseDto(
                        tutor.getId(),
                        tutor.getName(),
                        tutor.getAvatar(),
                        tutor.getUser() != null ? tutor.getUser().getEmail() : null,
                        tutor.getExperience(),
                        tutor.getRating()
                ))
                .toList();
    }
}
