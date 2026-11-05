package com.vibecoding.tracker.web.dto;

import com.vibecoding.tracker.domain.SystemUser;
import com.vibecoding.tracker.domain.UserRole;

public record CurrentUserResponse(Long id, String username, String displayName, UserRole role,
                                  Long customerId, String customerName) {
    public static CurrentUserResponse from(SystemUser user) {
        return new CurrentUserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole(),
            user.getCustomer() == null ? null : user.getCustomer().getId(),
            user.getCustomer() == null ? null : user.getCustomer().getName());
    }
}
