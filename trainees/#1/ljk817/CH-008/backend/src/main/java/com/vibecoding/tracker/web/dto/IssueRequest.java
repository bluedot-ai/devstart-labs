package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequest(
    Long customerId,
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 5000) String description,
    @NotBlank @Size(max = 80) String category,
    @NotNull Severity severity,
    @Size(max = 80) String assignee
) {}
