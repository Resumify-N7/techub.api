package com.techub.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(nullable = false)
    private Boolean ativo = true;

    @PrePersist
    protected void ensureAtivo() {
        if (ativo == null) {
            ativo = true;
        }
    }
}