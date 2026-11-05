package com.vibecoding.tracker.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 80) String contactName,
    @Size(max = 40) String phone,
    @Email @Size(max = 160) String email,
    @Size(max = 500) String description
) {}
