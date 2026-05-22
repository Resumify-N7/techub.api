package com.techub.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_alunos")
@Getter
@Setter


public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Integer semestre = 1;

    @Column(nullable = true)
    private String bio;

    @ManyToOne
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @ManyToOne
    @JoinColumn(name = "curso_atual_id")
    private Course course;

    @Column
    private Integer pontuacao = 0;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Summary> summaries = new ArrayList<>();
}