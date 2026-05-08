package com.techub.api.dto;

import com.techub.api.domain.Role;

import java.util.Date;

public record UserGetResponseDTO (
        Long userId,
        String email,
        Role role,
        Date createdAt
){ }
