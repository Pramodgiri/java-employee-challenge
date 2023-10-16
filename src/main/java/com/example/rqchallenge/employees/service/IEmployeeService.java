package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.entities.Employee;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    List<Employee> getAllEmployees() throws IOException;

    List<Employee> getEmployeesByNameSearch(String searchString) throws Exception;

    Employee getEmployeeById(String id) throws Exception;

    Integer getHighestSalaryOfEmployees() throws Exception;

    List<String> getTopTenHighestEarningEmployeeNames() throws Exception;

    Employee createEmployee(Map<String, Object> employeeInput) throws Exception;

    String deleteEmployeeById(String id) throws Exception;
}
