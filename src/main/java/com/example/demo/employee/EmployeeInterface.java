package com.example.demo.employee;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository interface for Employee entities.
 *
 * <p>Extends Hypersistence BaseJpaRepository to gain extra utility methods and
 * Spring Data's PagingAndSortingRepository for pagination and sorting support.</p>
 */
public interface EmployeeInterface extends  BaseJpaRepository<Employee, Long> , PagingAndSortingRepository<Employee, Long> {
    /**
     * Returns all employees using a native SQL query.
     *
     * @return list of all employees
     */
    @Query(nativeQuery = true , value = "SELECT  * FROM employee")
    List<Employee> findAll();
}
