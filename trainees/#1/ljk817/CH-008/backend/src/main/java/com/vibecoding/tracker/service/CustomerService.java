package com.vibecoding.tracker.service;

import com.vibecoding.tracker.domain.Customer;
import com.vibecoding.tracker.repository.CustomerRepository;
import com.vibecoding.tracker.web.dto.CustomerRequest;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    public CustomerService(CustomerRepository repository) { this.repository = repository; }

    public Page<Customer> list(Pageable pageable) { return repository.findAll(pageable); }
    public Customer get(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("客户不存在")); }

    @Transactional
    public Customer create(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return repository.save(customer);
    }

    @Transactional
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = get(id);
        apply(customer, request);
        return customer;
    }

    @Transactional
    public Customer setActive(Long id, boolean active) {
        Customer customer = get(id);
        customer.setActive(active);
        return customer;
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setContactName(request.contactName());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setDescription(request.description());
    }
}
