package com.techub.api.dto;

import com.techub.api.domain.Role;

public record AuthResponse (
        Boolean status,
        Long id,
        Role role
) {}
