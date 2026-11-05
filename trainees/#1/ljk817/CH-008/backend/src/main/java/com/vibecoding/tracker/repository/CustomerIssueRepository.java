package com.vibecoding.tracker.repository;

import com.vibecoding.tracker.domain.CustomerIssue;
import com.vibecoding.tracker.domain.IssueStatus;
import com.vibecoding.tracker.domain.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerIssueRepository extends JpaRepository<CustomerIssue, Long>, JpaSpecificationExecutor<CustomerIssue> {
    Page<CustomerIssue> findByCustomerId(Long customerId, Pageable pageable);
    long countByStatus(IssueStatus status);
    long countBySeverity(Severity severity);
    long countByCategory(String category);
}
