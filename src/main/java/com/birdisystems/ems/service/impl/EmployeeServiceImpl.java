package com.birdisystems.ems.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.birdisystems.ems.exception.ResourceNotFoundException;
import com.birdisystems.ems.model.Employee;
import com.birdisystems.ems.repository.EmployeeRepository;
import com.birdisystems.ems.service.EmployeeService;

@Repository
public class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public Employee addEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}

	@Override
	public Employee updateEmployee(Long id, Employee employee) throws ResourceNotFoundException {
	    Optional<Employee> optionalExistingEmployee = employeeRepository.findById(id);
	    if (optionalExistingEmployee.isPresent()) {
	        Employee existingEmployee = optionalExistingEmployee.get();
	        existingEmployee.setName(employee.getName());
	        existingEmployee.setDepartment(employee.getDepartment());
	        existingEmployee.setPosition(employee.getPosition());
	        existingEmployee.setSalary(employee.getSalary());
	        return employeeRepository.save(existingEmployee);
	    } else {
	        throw new ResourceNotFoundException("Employee not found with id: " + id);
	    }
	}


	@Override
	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}

	@Override
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	@Override
	public Employee getEmployeeById(Long id) throws ResourceNotFoundException {
	    Optional<Employee> optionalExistingEmployee = employeeRepository.findById(id);
		 if (optionalExistingEmployee.isPresent()) {
		        return optionalExistingEmployee.get();
		    } else {
		        throw new ResourceNotFoundException("Employee not found with id: " + id);
		    }
	}
}
