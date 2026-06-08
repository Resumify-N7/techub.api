package com.techub.api.dto;

import com.techub.api.domain.Role;

public record AuthResponse (
        Boolean status,
        Long id,
        Long studentId,
        Long professorId,
        Role role
) {}
