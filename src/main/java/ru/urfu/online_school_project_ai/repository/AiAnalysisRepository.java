package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.AiAnalysis;

import java.util.UUID;
import java.util.List;
import ru.urfu.online_school_project_ai.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, UUID> {
    List<AiAnalysis> findByStudent(User student);

    @Query("SELECT AVG(a.score) FROM AiAnalysis a WHERE a.student = :student AND a.score IS NOT NULL")
    Double getAverageScoreByStudent(@Param("student") User student);
}
