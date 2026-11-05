package com.vibecoding.tracker.web.dto;
import jakarta.validation.constraints.*;
public record CategoryRequest(@NotBlank @Size(max=80) String name, @Size(max=300) String description) {}
