package com.techub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_seguidores")
@Getter
@Setter
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student follower;

    @ManyToOne
    private Student following;

}
