package ru.urfu.online_school_project_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.online_school_project_ai.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
}

