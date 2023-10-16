package com.example.rqchallenge;

import com.example.rqchallenge.employees.entities.Employee;
import com.example.rqchallenge.employees.rest.EmployeeController;
import com.example.rqchallenge.employees.utils.AppConstants;
import com.example.rqchallenge.employees.utils.HttpRestUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class RqChallengeApplicationTests {

    @Autowired
    EmployeeController employeeController;

    MockedStatic<HttpRestUtil> httpRestUtilMock;

    @BeforeEach
    void setUp() {
        httpRestUtilMock = Mockito.mockStatic(HttpRestUtil.class);
    }

    @AfterEach
    public void close() {
        httpRestUtilMock.close();
    }

    @Test
    public void testCreateEmployeeSuccess() {

        Optional<String> employeeString = Optional.of("{\"name\":\"pramod\"}");
        Map<String,Object> empMap = Map.of("name","pramod");
        String response = "{\"status\": \"success\",\"data\": {\"employee_name\": \"pramod\",\"employee_salary\": \"5000\",\"employee_age\": \"35\",\"id\": 3}}";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.CREATE_EMPLOYEE_URL, httpHeaders,
                        AppConstants.POST, employeeString))
                .thenReturn(Optional.of(response));

        ResponseEntity<Employee> employee = employeeController.createEmployee(empMap);
        assertEquals(HttpStatus.CREATED, employee.getStatusCode());
        assertEquals(3, employee.getBody().getId());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));
    }

    @Test
    public void testCreateEmployeeMockData() {

        Map<String, Object> employeeInput = Map.of("name", "pramod", "salary", "5000", "age", "35");
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.CREATE_EMPLOYEE_URL, null,
                        AppConstants.POST, Optional.ofNullable(new JSONObject(employeeInput).toString())));

        ResponseEntity<Employee> employee = employeeController.createEmployee(employeeInput);
        assertEquals(HttpStatus.CREATED, employee.getStatusCode());
        assertEquals(3, employee.getBody().getId());
        assertEquals("pramod" + AppConstants.MOCK_DATA, employee.getBody().getName());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));
    }

    @Test
    public void testDeleteEmployeeByIdSuccess() {
        String response = "{\"status\":\"success\",\"data\":{\"id\":965,\"employee_name\":\"Pramod Giri\",\"employee_salary\":5000,\"employee_age\":87,\"profile_image\":\"\"}}";
        String deleteAPIRes = "{\"status\": \"success\",\"message\": \"successfully! deleted Records\"}";
        String id = "965";

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL +"/"+ id, null,
                        AppConstants.GET, Optional.empty()))
                .thenReturn(Optional.of(response));

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.DELETE_EMPLOYEE_URL +"/"+ id, null,
                        AppConstants.DELETE, Optional.empty()))
                .thenReturn(Optional.of(deleteAPIRes));

        ResponseEntity<String> employee = employeeController.deleteEmployeeById(id);
        assertEquals(HttpStatus.OK, employee.getStatusCode());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(2));
    }

    @Test
    public void testDeleteEmployeeByIdNotFound() {
        String defaultResponse = "{\"status\":\"success\",\"data\": null }";
        String deletionResp = "{\"status\": \"success\",\"message\": \"successfully! deleted Records\"}";
        int id = 3;

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL +"/"+ id, null,
                        AppConstants.GET, Optional.empty()))
                .thenReturn(Optional.of(defaultResponse));

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.DELETE_EMPLOYEE_URL +"/"+ id, null,
                        AppConstants.DELETE, Optional.empty()))
                .thenReturn(Optional.of(deletionResp));

        ResponseEntity<String> employee = employeeController.deleteEmployeeById(id + "");
        assertEquals(HttpStatus.NOT_FOUND, employee.getStatusCode());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));

    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        String response = "{\"status\":\"success\",\"data\":{\"id\":3,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"}}";
        String id = "3";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL +"/"+ id,
                        null, AppConstants.GET, Optional.empty()))
                .thenReturn(Optional.of(response));
        ResponseEntity<Employee> employee = employeeController.getEmployeeById(id);
        assertEquals(HttpStatus.OK, employee.getStatusCode());
        assertEquals(id, String.valueOf(employee.getBody().getId()));
    }

    @Test()
    public void testGetEmployeeByIdMockData(){
        String id = "3";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL +"/"+ id,
                        null, AppConstants.GET, Optional.empty()));

        ResponseEntity<Employee> employee = employeeController.getEmployeeById(id + "");
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));
        assertEquals(HttpStatus.OK, employee.getStatusCode());
        assertEquals(id, String.valueOf(employee.getBody().getId()));
        assertEquals(AppConstants.MOCK_DATA, employee.getBody().getName());
    }

    @Test
    public void testGetHighestSalaryOfEmployeesSuccess() {

        String response = "{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":14,\"employee_name\":\"Haley Kennedy\",\"employee_salary\":313500,\"employee_age\":43,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Bradley Greer\",\"employee_salary\":132000,\"employee_age\":41,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}"; //"{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":2,\"employee_name\":\"Garrett Winters\",\"employee_salary\":170750,\"employee_age\":63,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Cedric Kelly\",\"employee_salary\":433060,\"employee_age\":22,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null, AppConstants.GET, Optional.empty()))
                .thenReturn(Optional.of(response));

        ResponseEntity<Integer> salary = employeeController.getHighestSalaryOfEmployees();
        assertEquals(320800, salary.getBody());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));
    }

    @Test
    public void testGetHighestSalaryOfEmployeesMockData() {

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null, AppConstants.GET, Optional.empty()));

        ResponseEntity<Integer> salary = employeeController.getHighestSalaryOfEmployees();
        assertEquals(Integer.MAX_VALUE, salary.getBody());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));

    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesSuccess() {
        String response = "{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":2,\"employee_name\":\"Garrett Winters\",\"employee_salary\":170750,\"employee_age\":63,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Cedric Kelly\",\"employee_salary\":433060,\"employee_age\":22,\"profile_image\":\"\"},{\"id\":5,\"employee_name\":\"Airi Satou\",\"employee_salary\":162700,\"employee_age\":33,\"profile_image\":\"\"},{\"id\":6,\"employee_name\":\"Brielle Williamson\",\"employee_salary\":372000,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":7,\"employee_name\":\"Herrod Chandler\",\"employee_salary\":137500,\"employee_age\":59,\"profile_image\":\"\"},{\"id\":8,\"employee_name\":\"Rhona Davidson\",\"employee_salary\":327900,\"employee_age\":55,\"profile_image\":\"\"},{\"id\":9,\"employee_name\":\"Colleen Hurst\",\"employee_salary\":205500,\"employee_age\":39,\"profile_image\":\"\"},{\"id\":10,\"employee_name\":\"Sonya Frost\",\"employee_salary\":103600,\"employee_age\":23,\"profile_image\":\"\"},{\"id\":11,\"employee_name\":\"Jena Gaines\",\"employee_salary\":90560,\"employee_age\":30,\"profile_image\":\"\"},{\"id\":12,\"employee_name\":\"Quinn Flynn\",\"employee_salary\":342000,\"employee_age\":22,\"profile_image\":\"\"},{\"id\":13,\"employee_name\":\"Charde Marshall\",\"employee_salary\":470600,\"employee_age\":36,\"profile_image\":\"\"},{\"id\":14,\"employee_name\":\"Haley Kennedy\",\"employee_salary\":313500,\"employee_age\":43,\"profile_image\":\"\"},{\"id\":15,\"employee_name\":\"Tatyana Fitzpatrick\",\"employee_salary\":385750,\"employee_age\":19,\"profile_image\":\"\"},{\"id\":16,\"employee_name\":\"Michael Silva\",\"employee_salary\":198500,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":17,\"employee_name\":\"Paul Byrd\",\"employee_salary\":725000,\"employee_age\":64,\"profile_image\":\"\"},{\"id\":18,\"employee_name\":\"Gloria Little\",\"employee_salary\":237500,\"employee_age\":59,\"profile_image\":\"\"},{\"id\":19,\"employee_name\":\"Bradley Greer\",\"employee_salary\":132000,\"employee_age\":41,\"profile_image\":\"\"},{\"id\":20,\"employee_name\":\"Dai Rios\",\"employee_salary\":217500,\"employee_age\":35,\"profile_image\":\"\"},{\"id\":21,\"employee_name\":\"Jenette Caldwell\",\"employee_salary\":345000,\"employee_age\":30,\"profile_image\":\"\"},{\"id\":22,\"employee_name\":\"Yuri Berry\",\"employee_salary\":675000,\"employee_age\":40,\"profile_image\":\"\"},{\"id\":23,\"employee_name\":\"Caesar Vance\",\"employee_salary\":106450,\"employee_age\":21,\"profile_image\":\"\"},{\"id\":24,\"employee_name\":\"Doris Wilder\",\"employee_salary\":85600,\"employee_age\":23,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL, null,
                        AppConstants.GET, Optional.empty()))
                .thenReturn(Optional.of(response));

        ResponseEntity<List<String>> employees = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(10, employees.getBody().size());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));


    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesMockData() {

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL, null,
                        AppConstants.GET, Optional.empty()));

        ResponseEntity<List<String>> employees = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(1, employees.getBody().size());
        assertEquals(AppConstants.MOCK_DATA, employees.getBody().get(0));
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));
    }

    @Test
    void testGetAllEmployeesSuccess() throws IOException {

        String response = "{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":14,\"employee_name\":\"Haley Kennedy\",\"employee_salary\":313500,\"employee_age\":43,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Bradley Greer\",\"employee_salary\":132000,\"employee_age\":41,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null,
                        AppConstants.GET,
                        Optional.empty()))
                .thenReturn(Optional.of(response));

        ResponseEntity<List<Employee>> employees = employeeController.getAllEmployees();
        assertNotEquals(null, employees.getBody());
        assertEquals(4, employees.getBody().size());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));

    }

    @Test
    void testGetAllEmployeesWithMockData() throws IOException {
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null,
                        AppConstants.GET,
                        Optional.empty()));

        ResponseEntity<List<Employee>> employees = employeeController.getAllEmployees();
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));
        assertEquals(3, employees.getBody().size());
        assertEquals(277000, employees.getBody().get(0).getSalary());
    }

    @Test
    public void testGetEmployeesByNameSearchSuccess() {
        String response = "{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":14,\"employee_name\":\"Haley Kennedy\",\"employee_salary\":313500,\"employee_age\":43,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Bradley Greer\",\"employee_salary\":132000,\"employee_age\":41,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}";
        String searchString = "Tiger";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null,
                        AppConstants.GET,
                        Optional.empty()))
                .thenReturn(Optional.of(response));
        ResponseEntity<List<Employee>> employees = employeeController.getEmployeesByNameSearch(searchString);
        assertEquals(1, employees.getBody().size());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));
    }

    @Test
    public void testGetEmployeesByNameSearchRecordNotFound() {
        String response = "{\"status\":\"success\",\"data\":[{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},{\"id\":14,\"employee_name\":\"Haley Kennedy\",\"employee_salary\":313500,\"employee_age\":43,\"profile_image\":\"\"},{\"id\":3,\"employee_name\":\"Ashton Cox\",\"employee_salary\":86000,\"employee_age\":66,\"profile_image\":\"\"},{\"id\":4,\"employee_name\":\"Bradley Greer\",\"employee_salary\":132000,\"employee_age\":41,\"profile_image\":\"\"}],\"message\":\"Successfully! All records has been fetched.\"}";
        String searchString = "Pramod";

        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null,
                        AppConstants.GET,
                        Optional.empty()))
                .thenReturn(Optional.of(response));

        ResponseEntity<List<Employee>> employees = employeeController.getEmployeesByNameSearch(searchString);
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));
        assertEquals(HttpStatus.NOT_FOUND, employees.getStatusCode());
        assertNull(employees.getBody());
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(1));
    }

    @Test
    public void testGetEmployeesByNameSearchMockData() {
        String searchString = "Pramod";
        httpRestUtilMock.when(
                () -> HttpRestUtil.callRestAPI(AppConstants.BASE_EMPLOYEES_URL,
                        null,
                        AppConstants.GET,
                        Optional.empty()));
        ResponseEntity<List<Employee>> employees = employeeController.getEmployeesByNameSearch(searchString);
        httpRestUtilMock.verify(() -> HttpRestUtil.callRestAPI(any(), any(), any(), any()),
                times(3));
        assertEquals(HttpStatus.OK, employees.getStatusCode());
        assertEquals(1, employees.getBody().size());
        assertEquals(searchString + AppConstants.MOCK_DATA, employees.getBody().get(0).getName());
    }

    @Test
    void contextLoads() {
    }

}
