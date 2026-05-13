package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "topics_id", nullable = false)
    private Topic topics;

    @Column(name = "task_number", nullable = false)
    private Integer task_number;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @ManyToMany(mappedBy = "tasks")
    private List<Homework> homeworks;

    public Task() {
    }

}
