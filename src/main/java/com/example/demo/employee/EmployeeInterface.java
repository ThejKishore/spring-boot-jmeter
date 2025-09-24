package com.example.demo.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeInterface extends JpaRepository<Employee, Long> {
}
