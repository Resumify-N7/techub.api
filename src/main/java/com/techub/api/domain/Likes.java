package com.techub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_curtidas")
@Getter
@Setter
public class Likes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O aluno que curtiu
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // O resumo que foi curtido
    @ManyToOne
    @JoinColumn(name = "summary_id")
    private Summary summary;
}
