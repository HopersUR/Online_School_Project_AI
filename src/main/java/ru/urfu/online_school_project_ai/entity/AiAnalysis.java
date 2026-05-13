package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.urfu.online_school_project_ai.entity.enums.AiErrorType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "aiAnalysis")
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = true)
    private Solution solution;


    @Column(columnDefinition = "TEXT")
    private String result_text;

    @Column
    private String efficiency;

    @Column
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ai_analysis_error_types", joinColumns = @JoinColumn(name = "ai_analysis_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "error_type")
    private List<AiErrorType> errorTypes;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    public AiAnalysis() {
    }


}
