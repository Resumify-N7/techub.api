package com.techub.api.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CourseChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Course novoCourse;

    private LocalDateTime dataTroca;
}