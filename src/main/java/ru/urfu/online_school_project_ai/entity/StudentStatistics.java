package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "student_statistics")
public class StudentStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Student student;

    @ManyToOne
    private Topic topic;

    @Column(name = "description")
    private String description;

    @Column(name = "task_number")
    private Integer task_number;

    @Column(name = "difficulty")
    private String difficulty;

    public StudentStatistics() {
    }

}

