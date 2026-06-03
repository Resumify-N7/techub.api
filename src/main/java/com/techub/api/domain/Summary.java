package com.techub.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "tb_resumos")
@Getter @Setter
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column
    private LocalDateTime datahora;

    @Column
    private Integer reports = 0;

    @Column
    private Boolean publico = true;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @OneToMany(mappedBy = "summary")
    @JsonIgnore
    private List<Favorites> favorites = new ArrayList<>();

        // Many-to-many via explicit join entity `TagSummary` (table: tags_resumos)
        @OneToMany(mappedBy = "summary", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<TagSummary> tagLinks = new ArrayList<>();
}