package com.example.rqchallenge.employees.utils;

import com.example.rqchallenge.employees.entities.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EmployeeMockDataProvider {

    public static final Logger logger = LoggerFactory.getLogger(EmployeeMockDataProvider.class);

    public List<Employee> getAllEmployees() {
        logger.debug("Providing raw data for getAllEmployee");
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(1, "Akash",  277000, "31", ""));
        list.add(new Employee(2, "Vikas", 277000, "60",""));
        list.add(new Employee(3, "Pradeep", 277000,"43", ""));
        return list;
    }

    public List<Employee> getEmployeeByName(String name) {
        logger.debug("Providing raw data for getEmployeeByName : {}", name);
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(3, name + AppConstants.MOCK_DATA,277000,"31", ""));
        return list;
    }

    public Employee getEmployeeById(int id) {
        logger.debug("Providing raw data for getEmployeeById : {}", id);
        return new Employee(id, AppConstants.MOCK_DATA,77000,"31","");
    }

    public Integer getHighestEmployeeSalary() {
        logger.debug("Providing raw data for getHighestEmployeeSalary as Max value of integer");
        return Integer.MAX_VALUE;
    }

    public List<String> getTopTenNames() {
        logger.debug("Providing raw data for getTopTenNames as single user");
        return List.of(AppConstants.MOCK_DATA);
    }

    public Employee createEmployee(Map<String, Object> emp) {
        logger.debug("Providing raw data for createEmployee");
        return new Employee(3, emp.get("name") + AppConstants.MOCK_DATA, 0, "0", "");
    }

    public String deleteEmployeeById(String id) {
        logger.debug("Providing raw data for deleteEmployeeById : {}", id);
        return AppConstants.MOCK_DATA;
    }
}
