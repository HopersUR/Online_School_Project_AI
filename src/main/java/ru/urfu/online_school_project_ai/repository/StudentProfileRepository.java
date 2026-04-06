package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.online_school_project_ai.entity.Student;

import java.util.UUID;

public interface StudentProfileRepository extends JpaRepository<Student, UUID> {
}
