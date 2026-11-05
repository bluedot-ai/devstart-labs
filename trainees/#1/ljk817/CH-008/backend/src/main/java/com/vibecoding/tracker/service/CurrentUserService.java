package com.vibecoding.tracker.service;

import com.vibecoding.tracker.domain.SystemUser;
import com.vibecoding.tracker.domain.UserRole;
import com.vibecoding.tracker.repository.SystemUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final SystemUserRepository users;

    public CurrentUserService(SystemUserRepository users) { this.users = users; }

    public SystemUser get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("当前用户不存在"));
    }

    public boolean isCustomer() { return get().getRole() == UserRole.CUSTOMER; }

    public Long customerId() {
        SystemUser user = get();
        if (user.getRole() != UserRole.CUSTOMER || user.getCustomer() == null) {
            throw new IllegalArgumentException("客户账号未绑定客户档案");
        }
        return user.getCustomer().getId();
    }
}
