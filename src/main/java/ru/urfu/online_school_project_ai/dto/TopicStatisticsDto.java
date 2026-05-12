package ru.urfu.online_school_project_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record TopicStatisticsDto (
    String topicName,
    long totalAttemptedTasks,
    long totalSuccessfullySolvedTasks,
    long totalSolutionsSubmitted

)
{
}