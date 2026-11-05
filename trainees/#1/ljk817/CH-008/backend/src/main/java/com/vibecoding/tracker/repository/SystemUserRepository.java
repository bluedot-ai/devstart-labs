package com.vibecoding.tracker.repository;

import com.vibecoding.tracker.domain.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import com.vibecoding.tracker.domain.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    @EntityGraph(attributePaths = "customer")
    Optional<SystemUser> findByUsername(String username);
    List<SystemUser> findByRoleInAndEnabledTrueOrderByDisplayNameAsc(List<UserRole> roles);
}
