package ru.urfu.online_school_project_ai.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;



@Setter
@Getter
@Entity
@Table(name = "solution_errors", uniqueConstraints = @UniqueConstraint(columnNames = {"solution_id", "error_id"}))
public class SolutionError {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "solution_id", nullable = false)
    private Solution solution;

    @ManyToOne
    @JoinColumn(name = "error_id", nullable = false)
    private Error error;

    public SolutionError() {}

}
