package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.*;
import java.time.LocalDateTime;

public record IssueResponse(
    Long id, String issueNo, Long customerId, String customerName,
    String title, String description, String category, Severity severity,
    IssueStatus status, String assignee, LocalDateTime createdAt,
    LocalDateTime updatedAt, LocalDateTime closedAt
) {
    public static IssueResponse from(CustomerIssue issue) {
        return new IssueResponse(issue.getId(), issue.getIssueNo(), issue.getCustomer().getId(),
            issue.getCustomer().getName(), issue.getTitle(), issue.getDescription(), issue.getCategory(),
            issue.getSeverity(), issue.getStatus(), issue.getAssignee(), issue.getCreatedAt(),
            issue.getUpdatedAt(), issue.getClosedAt());
    }
}
