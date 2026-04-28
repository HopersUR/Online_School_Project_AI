package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.Lesson;

import java.time.ZonedDateTime;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    long countByCreatedAtAfter(ZonedDateTime fromDate);

    long countByStudentProfileId(UUID studentId);

    java.util.List<Lesson> findAllByTutorProfileIdOrderByScheduledAtAsc(UUID tutorId);

    java.util.List<Lesson> findAllByStudentProfileIdOrderByScheduledAtAsc(UUID studentId);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM Lesson l WHERE l.meeting_link IS NULL AND l.scheduledAt BETWEEN :start AND :end")
    java.util.List<Lesson> findLessonsWithoutLinkStartingBetween(
        @org.springframework.data.repository.query.Param("start") ZonedDateTime start,
        @org.springframework.data.repository.query.Param("end") ZonedDateTime end
    );
}
