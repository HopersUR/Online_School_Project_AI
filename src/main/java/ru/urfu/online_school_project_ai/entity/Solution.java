package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "solution")
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String code;

    @ManyToMany
    @JoinTable(
        name = "solution_error",
        joinColumns = @JoinColumn(name = "solution_id"),
        inverseJoinColumns = @JoinColumn(name = "error_id")
    )
    private List<Error> errors;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column
    private String status;

    @Column(columnDefinition = "TEXT")
    private String aiRecommendation;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    public Solution() {
    }

}
