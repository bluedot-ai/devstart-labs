package com.vibecoding.tracker.repository;

import com.vibecoding.tracker.domain.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory,Long> {
    List<IssueCategory> findAllByOrderByNameAsc();
    Optional<IssueCategory> findByName(String name);
}
