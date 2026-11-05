package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.IssueStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(
    @NotNull IssueStatus status,
    @Size(max = 1000) String note
) {}
