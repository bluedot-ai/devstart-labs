package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.*;
import java.time.LocalDateTime;

public record UserResponse(Long id, String username, String displayName, UserRole role,
                           Long customerId, String customerName, boolean enabled, LocalDateTime createdAt) {
    public static UserResponse from(SystemUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole(),
            user.getCustomer() == null ? null : user.getCustomer().getId(),
            user.getCustomer() == null ? null : user.getCustomer().getName(),
            user.isEnabled(), user.getCreatedAt());
    }
}
