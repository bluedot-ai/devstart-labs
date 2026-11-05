package com.vibecoding.tracker.web;

import com.vibecoding.tracker.domain.Customer;
import com.vibecoding.tracker.service.CustomerService;
import com.vibecoding.tracker.web.dto.CustomerRequest;
import com.vibecoding.tracker.web.dto.ActiveUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENT','VIEWER')")
    public Page<Customer> list(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.list(pageable);
    }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','AGENT','VIEWER')")
    public Customer get(@PathVariable Long id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public Customer create(@Valid @RequestBody CustomerRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public Customer update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public Customer setActive(@PathVariable Long id, @Valid @RequestBody ActiveUpdateRequest request) {
        return service.setActive(id, request.active());
    }
}
