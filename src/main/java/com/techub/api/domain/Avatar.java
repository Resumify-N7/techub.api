package com.techub.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "tb_avatar")
@Getter
@Setter
public class Avatar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean male;

    @Column
    private String url;

    @Column
    private String description;

    @OneToMany(mappedBy = "avatar")
    @JsonIgnore
    private List<Student> students;
}