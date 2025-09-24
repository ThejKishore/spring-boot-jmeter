package com.example.demo;

import com.example.demo.employee.EmployeeInterface;
import com.example.demo.employee.EmployeeResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.POST;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class Demo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> helloRoute() {
        return route(GET("/hello"), Demo1Application::greetAnonymous)
                .andRoute(GET("/hello/{name}"), Demo1Application::greetUser);
    }

    private static ServerResponse greetAnonymous(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Hello world"));
    }

    private static ServerResponse greetUser(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Hello " + request.pathVariable("name")));
    }

    @Bean
    EmployeeResource employeeResource(EmployeeInterface employeeInterface) {
        return new EmployeeResource(employeeInterface);
    }

    @Bean
    RouterFunction<ServerResponse> employeeRoute(EmployeeResource employeeResource) {
        return route(POST("/employee"), employeeResource::save)
                .andRoute(GET("/employee"), employeeResource::fetchAll)
                .andRoute(GET("/employee/{id}"),employeeResource::fetchById);
    }
}
