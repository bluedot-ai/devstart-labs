package com.vibecoding.tracker.web.dto;

import jakarta.validation.constraints.NotNull;
public record UserEnabledRequest(@NotNull Boolean enabled) {}
