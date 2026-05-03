package com.hampcode.pagoya.auth.dto;

public record UserResponse(
    Long id,
    String email,
    Boolean verified,
    String role
) {}
