package com.vibecoding.tracker.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordRequest(
    @NotBlank @Size(max = 1000) String content,
    boolean customerVisible
) {}
