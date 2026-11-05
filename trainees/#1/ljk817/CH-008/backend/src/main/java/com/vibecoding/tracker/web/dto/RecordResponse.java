package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.IssueRecord;
import com.vibecoding.tracker.domain.IssueStatus;
import com.vibecoding.tracker.domain.UserRole;
import java.time.LocalDateTime;

public record RecordResponse(Long id, String content, String operatorName, UserRole authorRole,
                             boolean customerVisible, IssueStatus oldStatus,
                             IssueStatus newStatus, LocalDateTime createdAt) {
    public static RecordResponse from(IssueRecord record) {
        return new RecordResponse(record.getId(), record.getContent(), record.getOperatorName(),
            record.getAuthorRole(), record.isCustomerVisible(),
            record.getOldStatus(), record.getNewStatus(), record.getCreatedAt());
    }
}
