package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTopicsId(Long topicId);
    List<Task> findByDifficulty(String difficulty);
    List<Task> findByTopicsIdAndDifficulty(Long topicId, String difficulty);
}
