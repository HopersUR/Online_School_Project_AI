package ru.urfu.online_school_project_ai.dto;

import java.util.Map;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.urfu.online_school_project_ai.entity.enums.AiErrorType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatisticsDto {
    private long totalAttemptedTasks;
    private long totalSuccessfullySolvedTasks;
    private long totalSolutionsSubmitted;
    private Double averageAiScore;
    private Map<AiErrorType, Long> commonErrorsMatrix;
    private List<TopicStatisticsDto> topicStatistics;
}
