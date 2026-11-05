package com.vibecoding.tracker.web.dto;
import jakarta.validation.constraints.NotNull;
public record ActiveUpdateRequest(@NotNull Boolean active) {}
