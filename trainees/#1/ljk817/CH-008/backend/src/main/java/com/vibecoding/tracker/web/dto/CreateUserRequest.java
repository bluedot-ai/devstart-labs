package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.UserRole;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,60}") String username,
    @NotBlank @Size(min = 12, max = 72) String password,
    @NotBlank @Size(max = 80) String displayName,
    @NotNull UserRole role,
    Long customerId
) {}
