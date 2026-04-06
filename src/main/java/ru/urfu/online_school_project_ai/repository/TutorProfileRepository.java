package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.online_school_project_ai.entity.Tutor;

import java.util.UUID;

public interface TutorProfileRepository extends JpaRepository <Tutor, UUID> {
}
