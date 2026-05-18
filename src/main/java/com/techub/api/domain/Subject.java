package com.techub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_materias")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer semestre;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}
