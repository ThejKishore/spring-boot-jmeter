package com.example.demo.employee;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple DTO wrapper used to serialize a collection of employees.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employees {
    /**
     * The list of employee items in the response.
     */
    List<Employee> employees;
}
