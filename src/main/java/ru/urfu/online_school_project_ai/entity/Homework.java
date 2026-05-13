package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "homework")
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Column(name = "deadline", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deadline;

    @Column(name = "text_assignment", columnDefinition = "TEXT")
    private String textAssignment;

    @ManyToMany
    @JoinTable(
        name = "homework_tasks",
        joinColumns = @JoinColumn(name = "homework_id"),
        inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> tasks;

    public Homework() {
    }
}
