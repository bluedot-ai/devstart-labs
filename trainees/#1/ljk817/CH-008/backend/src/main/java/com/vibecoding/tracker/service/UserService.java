package com.vibecoding.tracker.service;

import com.vibecoding.tracker.domain.SystemUser;
import com.vibecoding.tracker.repository.SystemUserRepository;
import com.vibecoding.tracker.repository.CustomerRepository;
import com.vibecoding.tracker.domain.UserRole;
import com.vibecoding.tracker.web.dto.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {
    private final SystemUserRepository users;
    private final PasswordEncoder encoder;
    private final CustomerRepository customers;
    public UserService(SystemUserRepository users, PasswordEncoder encoder, CustomerRepository customers) {
        this.users = users; this.encoder = encoder; this.customers = customers;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() { return users.findAll().stream().map(UserResponse::from).toList(); }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (users.findByUsername(request.username()).isPresent()) throw new IllegalArgumentException("用户名已存在");
        SystemUser user = new SystemUser();
        user.setUsername(request.username().trim()); user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(encoder.encode(request.password())); user.setRole(request.role()); user.setEnabled(true);
        if (request.role() == UserRole.CUSTOMER) {
            if (request.customerId() == null) throw new IllegalArgumentException("客户账号必须绑定客户档案");
            var customer = customers.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("客户不存在"));
            if (!customer.isActive()) throw new IllegalArgumentException("不能为已停用客户创建账号");
            user.setCustomer(customer);
        } else if (request.customerId() != null) {
            throw new IllegalArgumentException("只有客户角色可以绑定客户档案");
        }
        try { return UserResponse.from(users.save(user)); }
        catch (DataIntegrityViolationException ex) { throw new IllegalArgumentException("用户名已存在"); }
    }

    @Transactional
    public UserResponse setEnabled(Long id, UserEnabledRequest request) {
        SystemUser user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        String current = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(current) && !request.enabled()) throw new IllegalArgumentException("不能停用当前登录账号");
        user.setEnabled(request.enabled());
        return UserResponse.from(user);
    }
}
