package com.vibecoding.tracker.web;

import com.vibecoding.tracker.service.UserService;
import com.vibecoding.tracker.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.vibecoding.tracker.domain.UserRole;
import com.vibecoding.tracker.repository.SystemUserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final SystemUserRepository users;
    public UserController(UserService service,SystemUserRepository users) { this.service = service;this.users=users; }
    @GetMapping("/assignable")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT','VIEWER')")
    public List<UserResponse> assignable(){return users.findByRoleInAndEnabledTrueOrderByDisplayNameAsc(List.of(UserRole.ADMIN,UserRole.AGENT)).stream().map(UserResponse::from).toList();}
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<UserResponse> list() { return service.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) { return service.create(request); }
    @PatchMapping("/{id}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse setEnabled(@PathVariable Long id, @Valid @RequestBody UserEnabledRequest request) {
        return service.setEnabled(id, request);
    }
}
