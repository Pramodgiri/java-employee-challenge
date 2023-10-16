package com.example.rqchallenge.employees.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

/**
 * This class is used to hold the employee object
 */
@AllArgsConstructor
@NoArgsConstructor
@Component
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    @JsonProperty("id")
    int id;
    @JsonAlias({"employee_name","name"})
    String name;
    @JsonAlias({"employee_salary","salary"})
    int salary;
    @JsonAlias({"employee_age","age"})
    String age;
    @JsonProperty("profile_image")
    String profileImage;

}
