package com.vibecoding.tracker.repository;

import com.vibecoding.tracker.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
