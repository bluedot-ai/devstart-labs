package com.vibecoding.tracker.repository;

import com.vibecoding.tracker.domain.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByIssueIdOrderByCreatedAtDesc(Long issueId);
    List<IssueRecord> findByIssueIdAndCustomerVisibleTrueOrderByCreatedAtDesc(Long issueId);
}
