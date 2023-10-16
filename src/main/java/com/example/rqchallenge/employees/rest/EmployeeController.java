package com.example.rqchallenge.employees.rest;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.employees.entities.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/employee")
public class EmployeeController implements IEmployeeController {

    public static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            if (Objects.isNull(employees) || employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(employees);
            }
        } catch (Exception ex) {
            logger.error("Error while fetching employees details. ERROR : ",ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Cacheable(value = "employeeByName", key = "#searchString")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        try {
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
            if (Objects.isNull(employees) || employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(employees);
            }
        } catch (Exception ex) {
            logger.error(String.format("Error while fetching employees details where name contains %s. ERROR : %s",searchString, ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Cacheable(value = "employeeById", key = "#id")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (Objects.isNull(employee)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(employee);
            }
        } catch (Exception ex) {
            logger.error(String.format("Error while searching the employee id %s",id));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Cacheable("highestSalaryEmployee")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            Integer salary = employeeService.getHighestSalaryOfEmployees();
            return ResponseEntity.status(HttpStatus.OK).body(salary);
        } catch (Exception ex) {
            logger.error("Error while fetching employees salary ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Cacheable("topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            List<String> employeesName = employeeService.getTopTenHighestEarningEmployeeNames();
            if(Objects.isNull(employeesName) || employeesName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(employeesName);
            }
        } catch (Exception ex) {
            logger.error(String.format("Error while fetching top 10 employees name having max salary",ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @CacheEvict(value = {"topTenHighestEarningEmployeeNames", "highestSalaryEmployee"}, allEntries = true)
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        try {
            Employee employee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.status(HttpStatus.CREATED).body(employee);
        } catch (Exception ex) {
            logger.error("Error while creating the employee",ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "topTenHighestEarningEmployeeNames", allEntries = true),
            @CacheEvict(value = "highestSalaryEmployee", allEntries = true),
            @CacheEvict(value = "employeeByName", allEntries = true),
            @CacheEvict(value = "employeeById", key = "#id")
    })
    public ResponseEntity<String> deleteEmployeeById(String id) {
        try {
             String employeeName = employeeService.deleteEmployeeById(id);
            if(Objects.isNull(employeeName)) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(employeeName);
            }
        } catch (Exception ex) {
            logger.error(String.format("Error while deleting the employee id %s",id));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
