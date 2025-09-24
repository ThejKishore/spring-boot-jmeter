package com.example.demo.employee;

import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.util.List;


@AllArgsConstructor
public class EmployeeResource {

    private final EmployeeInterface employeeInterface;

    public ServerResponse save(ServerRequest serverRequest) throws ServletException, IOException {
        Employee employee = serverRequest.body(Employee.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeeInterface.save(employee));
    }

    public ServerResponse fetchAll(ServerRequest serverRequest) throws ServletException, IOException {
        List<Employee> all = employeeInterface.findAll();
        Employees employees = Employees.builder().employees(all).build();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employees);
    }

    public ServerResponse fetchById(ServerRequest serverRequest) throws ServletException, IOException {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        Employee employee = employeeInterface.findById(id).get();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(employee);
    }
}
