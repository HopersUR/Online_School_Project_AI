package ru.urfu.online_school_project_ai.dto;

import java.util.Map;
import java.util.List;
import ru.urfu.online_school_project_ai.entity.enums.AiErrorType;

public record StudentStatisticsDto (
     long totalAttemptedTasks,
     long totalSuccessfullySolvedTasks,
     long totalSolutionsSubmitted,
     Double averageAiScore,
     Map<AiErrorType, Long> commonErrorsMatrix,
     List<TopicStatisticsDto> topicStatistics
)
{

}
