package com.birdisystems.ems.service;

import com.birdisystems.ems.exception.ResourceNotFoundException;
import com.birdisystems.ems.model.Employee;

import java.util.List;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee) throws ResourceNotFoundException;
    void deleteEmployee(Long id);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(String id) throws ResourceNotFoundException;
}
