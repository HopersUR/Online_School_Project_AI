package ru.urfu.online_school_project_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicStatisticsDto {
    private String topicName;
    private long totalAttemptedTasks;
    private long totalSuccessfullySolvedTasks;
    private long totalSolutionsSubmitted;
}

