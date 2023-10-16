package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.entities.Employee;
import com.example.rqchallenge.employees.entities.EmployeesResponse;
import com.example.rqchallenge.employees.entities.SingleEmployeeResponse;
import com.example.rqchallenge.employees.utils.AppConstants;
import com.example.rqchallenge.employees.utils.EmployeeMockDataProvider;
import com.example.rqchallenge.employees.utils.HttpRestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService{

    public static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EmployeeMockDataProvider employeeMockDataProvider;

    /**
     * This API is used to get all the employees from data store
     * @return
     * @throws Exception
     */
    @Retry(name="employeeServiceImpl",fallbackMethod = "getAllEmployeesMock")
    public List<Employee> getAllEmployees() throws IOException {
        Optional<String> response = HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                null ,AppConstants.GET, Optional.empty());
        EmployeesResponse employeesResponse = objectMapper.readValue(response.get(), EmployeesResponse.class);
        logger.debug("Employees are fetched successfully and size is %d",employeesResponse.getData().size());
        return !employeesResponse.getData().isEmpty() ? employeesResponse.getData() : null;
    }

    /**
     * This Method is used to get all employees whose name contains input string.
     * @param searchString
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "getEmployeesByNameSearchMock")
    public List<Employee> getEmployeesByNameSearch(String searchString) throws Exception {
        logger.debug(String.format("Searching for employees whose name contains %s ", searchString));
        List<Employee> employees =  getAllEmployees();
        logger.debug(String.format("There are %d  employees whose name contains %s ",employees.size(), searchString));
        return employees.stream().filter(employee -> employee.getName()
                .contains(searchString)).collect(Collectors.toList());
    }

    /**
     * This method is used to get the Employee details by ID.
     * @param id
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "getEmployeeByIdMock")
    public Employee getEmployeeById(String id) throws Exception {
        logger.debug(String.format("Searching for %s employee ", id));
        Optional<String> response = HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL+"/"+id,
                null, AppConstants.GET, Optional.empty());
        SingleEmployeeResponse employeeResponse = objectMapper.readValue(response.get(),
                SingleEmployeeResponse.class);
        logger.debug("Employee are fetched successfully");
        return employeeResponse.getData()!=null ? employeeResponse.getData() : null;
    }

    /**
     * This method is used to get employee's Highest salary
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "getHighestSalaryOfEmployeesMock")
    public Integer getHighestSalaryOfEmployees() throws Exception {
        logger.debug("getHighestSalaryOfEmployees API STARTS");
        List<Employee> employees =  getAllEmployees();
        Optional<Employee> employee =  employees.stream().max(Comparator.comparingDouble(Employee::getSalary));
        logger.debug("getHighestSalaryOfEmployees API ENDS");
        return employee.map(Employee::getSalary).orElse(null);
    }

    /**
     * This Method is used to get the top ten Highest Earning Employee Names list
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "getTopTenHighestEarningEmployeeNamesMock")
    public List<String> getTopTenHighestEarningEmployeeNames() throws Exception {
        List<Employee> employees =  getAllEmployees();
        logger.debug(String.format("No of Employees are %d",employees.size()));
        return employees.stream().sorted(Comparator.comparingDouble(Employee::getSalary)
                .reversed())
                .limit(10)
                .map(Employee::getName).collect(Collectors.toList());
    }

    /**
     * This method is used to create a new Employee
     * @param employeeInput
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "createEmployeeMock")
    public Employee createEmployee(Map<String, Object> employeeInput) throws Exception {
        Optional<String> empInputs = Optional.of(objectMapper.writeValueAsString(employeeInput));
        logger.debug(String.format("Creating employee using below inputs : \n %s", empInputs));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Optional<String> response = HttpRestUtil.callRestAPI(AppConstants.CREATE_EMPLOYEE_URL,
                httpHeaders, AppConstants.POST , empInputs);
        SingleEmployeeResponse employeeResponse = objectMapper.readValue(response.get(),
                SingleEmployeeResponse.class);
        return employeeResponse.getData()!=null ? employeeResponse.getData() : null;
    }

    /**
     * This method is used to delete the employee by using ID
     * @param id
     * @return
     * @throws Exception
     */
    @Retry(name="employeeService",fallbackMethod = "deleteEmployeeByIdMock")
    public String deleteEmployeeById(String id) throws Exception {
        //we need employee of this ID
        Employee employee = getEmployeeById(id);
        if (employee != null) {
            logger.debug(String.format("Deleting the employee having employee id : %s", id));
            Optional<String> response = HttpRestUtil.callRestAPI(AppConstants.DELETE_EMPLOYEE_URL + "/" + id,
                    null, AppConstants.DELETE, Optional.empty());
            String status = "";
            if (response.isPresent()) {
                //parse the response of delete API
                JsonNode jsonNode = objectMapper.readTree(response.get());
                status = jsonNode.get(AppConstants.STATUS).asText();
            }
            if (status.equalsIgnoreCase(AppConstants.SUCCESS)) {
                return employee.getName();
            }
        }
        return null;
    }

    /**
     * This is fallback method for getAllEmployees.
     * @param t
     * @return
     */
    public List<Employee> getAllEmployeesMock(Throwable t) {
        return employeeMockDataProvider.getAllEmployees();
    }

    /**
     * This is fallback Method for getEmployeesByNameSearch
     * @param searchString
     * @param t
     * @return
     */
    public List<Employee> getEmployeesByNameSearchMock(String searchString, Throwable t) {
        return employeeMockDataProvider.getEmployeeByName(searchString);
    }

    /**
     * This is fallback method for getEmployeeById;
     * @param id
     * @param t
     * @return
     */
    public Employee getEmployeeByIdMock(String id, Throwable t) {
        try {
            return employeeMockDataProvider.getEmployeeById(Integer.parseInt(id));
        } catch (Exception e) {
            logger.error("Error while getting employee",id);
            return employeeMockDataProvider.getEmployeeById(Integer.parseInt("0"));
        }
    }

    /**
     * This is fallback method for getHighestSalaryOfEmployees
     * @param t
     * @return
     */
    public Integer getHighestSalaryOfEmployeesMock(Throwable t) {
        return employeeMockDataProvider.getHighestEmployeeSalary();
    }

    /**
     * This is fallback method for getTopTenHighestEarningEmployeeNames
     * @param t
     * @return
     */
    public List<String> getTopTenHighestEarningEmployeeNamesMock(Throwable t) {
        return employeeMockDataProvider.getTopTenNames();
    }

    /**
     * This is fallback method for createEmployee
     * @param emp
     * @param t
     * @return
     */
    public Employee createEmployeeMock(Map<String, Object> emp, Throwable t) {
        return employeeMockDataProvider.createEmployee(emp);
    }

    /**
     * This is fallback method for deleteEmployeeById
     * @param id
     * @param t
     * @return
     */
    public String deleteEmployeeByIdMock(String id, Throwable t) {
        return employeeMockDataProvider.deleteEmployeeById(id);
    }
}
