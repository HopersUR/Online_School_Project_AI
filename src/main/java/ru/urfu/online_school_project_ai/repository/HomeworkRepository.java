package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.Homework;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, UUID> {
    Optional<Homework> findByLessonId(UUID lessonId);
}
