package com.example.demo;

import com.example.demo.employee.EmployeeInterface;
import com.example.demo.employee.EmployeeResource;
import com.example.demo.util.HttpException;
import com.example.demo.util.LoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * Entry point and configuration for the Demo1 Spring Boot application.
 *
 * <p>This application demonstrates functional routing with Spring WebMvc.fn and
 * Spring Data JPA repositories. Repositories are configured to use Hypersistence
 * BaseJpaRepository implementation to avoid misinterpretation of repository methods
 * like {@code update(Object)} as derived query methods.</p>
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo",
        repositoryBaseClass = io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl.class)
public class Demo1Application {


    public static final String MESSAGE = "message";

    /**
     * Boots the Spring application using functional bean registration.
     *
     * @param args application arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Demo1Application.class);
        app.addInitializers(new FunctionalBeanRegistrar());
        app.run(args);
    }

    /**
     * Registers functional beans (resources, filters, and router functions).
     * This avoids component scanning and keeps configuration explicit.
     */
    static class FunctionalBeanRegistrar implements ApplicationContextInitializer<GenericApplicationContext> {

        /**
         * Programmatically registers beans needed by the application at startup.
         *
         * @param context the configurable application context
         */
        @Override
        public void initialize(GenericApplicationContext context) {
            context.registerBean(EmployeeResource.class,
                    () -> new EmployeeResource(context.getBean(EmployeeInterface.class)));
            context.registerBean(LoggingFilter.class, LoggingFilter::new);
            context.registerBean(RouterFunction.class, () -> helloRoute()
                    .andOther(employeeRoute(context.getBean(EmployeeResource.class))));
        }
    }


    /**
     * Defines greeting routes using functional WebMvc.fn routing.
     *
     * @return router function exposing /hello and /hello/{name}
     */
    static RouterFunction<ServerResponse> helloRoute() {
        return route()
                .GET("/hello", Demo1Application::greetAnonymous)
                .GET("/hello/{name}",Demo1Application::greetUser)
                .build();
    }


    /**
     * Returns a generic greeting message.
     *
     * @param request the incoming server request
     * @return 200 OK with a JSON greeting payload
     */
    private static ServerResponse greetAnonymous(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(MESSAGE, "Hello world"));
    }

    /**
     * Returns a personalized greeting using the path variable "name".
     *
     * @param request the incoming server request
     * @return 200 OK with a JSON greeting payload
     */
    private static ServerResponse greetUser(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(MESSAGE, "Hello " + request.pathVariable("name")));
    }

    /**
     * Defines employee-related routes and centralized error handling for resource methods.
     *
     * @param employeeResource the employee resource handler
     * @return router function exposing CRUD routes under /employee
     */
    static RouterFunction<ServerResponse> employeeRoute(EmployeeResource employeeResource) {
        return route()
                .POST("/employee",employeeResource::save)
                .GET("/employee",employeeResource::fetchAll)
                .GET("/employee/{id}",employeeResource::fetchById)
                .PUT("/employee/{id}",employeeResource::update)
                .onError(HttpException.class, (ex,req) ->
                        ServerResponse
                                .status(((HttpException) ex).getStatus())
                                .body(Map.of(MESSAGE, ex.getMessage()))
                ).onError(Exception.class, (ex,req) ->
                                ServerResponse
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of(MESSAGE, ex.getMessage()))
                )
                .build();

    }


}
