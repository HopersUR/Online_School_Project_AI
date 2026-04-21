package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.Solution;
import ru.urfu.online_school_project_ai.entity.Task;
import ru.urfu.online_school_project_ai.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, UUID> {
    Optional<Solution> findByUserAndTask(User user, Task task);
}
