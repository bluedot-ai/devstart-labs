package com.vibecoding.tracker.config;

import com.vibecoding.tracker.domain.*;
import com.vibecoding.tracker.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitialAdminInitializer implements ApplicationRunner {
    private final SystemUserRepository users;
    private final PasswordEncoder encoder;
    private final String username;
    private final String password;

    public InitialAdminInitializer(SystemUserRepository users, PasswordEncoder encoder,
        @Value("${app.initial-admin.username:admin}") String username,
        @Value("${app.initial-admin.password:}") String password) {
        this.users = users; this.encoder = encoder; this.username = username; this.password = password;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) return;
        if (password == null || password.length() < 12) {
            throw new IllegalStateException("首次启动必须设置至少12位的 INITIAL_ADMIN_PASSWORD");
        }
        SystemUser admin = new SystemUser();
        admin.setUsername(username.trim());
        admin.setDisplayName("系统管理员");
        admin.setPasswordHash(encoder.encode(password));
        admin.setRole(UserRole.ADMIN);
        users.save(admin);
    }
}
