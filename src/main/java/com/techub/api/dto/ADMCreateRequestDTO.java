package com.techub.api.dto;

public record ADMCreateRequestDTO (
    String email,
    String senha,
    String username
) {}
