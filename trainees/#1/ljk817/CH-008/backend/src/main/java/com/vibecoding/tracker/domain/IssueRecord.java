package com.vibecoding.tracker.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_record")
public class IssueRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id")
    private CustomerIssue issue;
    @Column(nullable = false, length = 1000)
    private String content;
    @Column(name = "operator_name", nullable = false, length = 80)
    private String operatorName;
    @Column(name = "customer_visible", nullable = false)
    private boolean customerVisible;
    @Enumerated(EnumType.STRING) @Column(name = "author_role", length = 20)
    private UserRole authorRole;
    @Enumerated(EnumType.STRING) @Column(name = "old_status", length = 20)
    private IssueStatus oldStatus;
    @Enumerated(EnumType.STRING) @Column(name = "new_status", length = 20)
    private IssueStatus newStatus;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public CustomerIssue getIssue() { return issue; }
    public void setIssue(CustomerIssue issue) { this.issue = issue; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public boolean isCustomerVisible() { return customerVisible; }
    public void setCustomerVisible(boolean customerVisible) { this.customerVisible = customerVisible; }
    public UserRole getAuthorRole() { return authorRole; }
    public void setAuthorRole(UserRole authorRole) { this.authorRole = authorRole; }
    public IssueStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(IssueStatus oldStatus) { this.oldStatus = oldStatus; }
    public IssueStatus getNewStatus() { return newStatus; }
    public void setNewStatus(IssueStatus newStatus) { this.newStatus = newStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
