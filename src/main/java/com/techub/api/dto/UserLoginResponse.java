package com.techub.api.dto;

import com.techub.api.domain.Role;

public record UserLoginResponse(
        String message,
        String token,
        Boolean status,
        Long id,
        Long studentId,
        Long professorId,
        Role role
) {}