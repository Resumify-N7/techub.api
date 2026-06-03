package com.techub.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tags_resumos", uniqueConstraints = @UniqueConstraint(columnNames = {"summary_id", "tag_id"}))
@Getter
@Setter
public class TagSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "summary_id", nullable = false)
    private Summary summary;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tags tag;
}
