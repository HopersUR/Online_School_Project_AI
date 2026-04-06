package ru.urfu.online_school_project_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "date_exam")
    private Date date;

    @Column
    private String level;

    @ManyToMany(mappedBy = "students")
    private List<Tutor> tutors;

    public Student() {
    }

}