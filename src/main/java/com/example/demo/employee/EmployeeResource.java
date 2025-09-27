package com.example.demo.employee;

import com.example.demo.util.ResourceNotFoundException;
import jakarta.servlet.ServletException;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.util.List;


/**
 * HTTP resource handler for Employee operations used by functional routes.
 * Provides CRUD-like endpoints backed by a Spring Data JPA repository.
 */
public class EmployeeResource {

    private final EmployeeInterface employeeInterface;

    public EmployeeResource(EmployeeInterface employeeInterface) {
        this.employeeInterface = employeeInterface;
    }

    /**
     * Persists a new employee from the JSON body.
     *
     * @param serverRequest the HTTP request containing an Employee payload
     * @return 200 OK with the persisted entity
     * @throws ServletException if the request body cannot be parsed
     * @throws IOException      on I/O errors
     */
    public ServerResponse save(ServerRequest serverRequest) throws ServletException, IOException {
        Employee employee = serverRequest.body(Employee.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeeInterface.persist(employee));
    }

    /**
     * Updates an existing employee identified by path variable {id} using data from the JSON body.
     *
     * @param serverRequest the HTTP request with path variable and updated Employee payload
     * @return 200 OK with the updated entity
     * @throws ServletException if the request body cannot be parsed
     * @throws IOException      on I/O errors
     */
    public ServerResponse update(ServerRequest serverRequest) throws ServletException, IOException {
        Employee originalEmployee = employeeInterface.findById(Long.valueOf(serverRequest.pathVariable("id")))
                .orElseThrow(()->new ResourceNotFoundException("Employee not found"));
        Employee updatedEmployee = serverRequest.body(Employee.class);
        originalEmployee.setName(updatedEmployee.getName());
        originalEmployee.setEmail(updatedEmployee.getEmail());
        originalEmployee.setPhone(updatedEmployee.getPhone());
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeeInterface.update(originalEmployee));
    }

    /**
     * Retrieves all employees.
     *
     * @param serverRequest the HTTP request
     * @return 200 OK with a JSON array wrapper of employees
     */
    public ServerResponse fetchAll(ServerRequest serverRequest) {
        List<Employee> all = employeeInterface.findAll();
        Employees employees = Employees.builder().employees(all).build();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employees);
    }

    /**
     * Retrieves an employee by id.
     *
     * @param serverRequest the HTTP request containing the employee id path variable
     * @return 200 OK with the employee payload, or 404 if not found
     */
    public ServerResponse fetchById(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        Employee employee = employeeInterface.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employee);
    }
}
