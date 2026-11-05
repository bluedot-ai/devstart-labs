package com.vibecoding.tracker.web;

import com.vibecoding.tracker.repository.SystemUserRepository;
import com.vibecoding.tracker.web.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SystemUserRepository users;
    public AuthController(AuthenticationManager authenticationManager, SystemUserRepository users) {
        this.authenticationManager = authenticationManager; this.users = users;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) { return Map.of("token", token.getToken()); }

    @PostMapping("/login")
    public CurrentUserResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(request.username().trim(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        servletRequest.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return current(authentication);
    }

    @GetMapping("/me")
    public CurrentUserResponse current(Authentication authentication) {
        return users.findByUsername(authentication.getName()).map(CurrentUserResponse::from)
            .orElseThrow(() -> new BadCredentialsException("用户不存在"));
    }
}
