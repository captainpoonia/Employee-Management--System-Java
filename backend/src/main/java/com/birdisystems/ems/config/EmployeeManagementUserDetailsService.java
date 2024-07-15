package com.birdisystems.ems.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.birdisystems.ems.model.Employee;
import com.birdisystems.ems.repository.EmployeeRepository;
import com.birdisystems.ems.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
public class EmployeeManagementUserDetailsService implements UserDetailsService {

    private EmployeeService employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.getEmployeeById(username).orElseThrow(() -> new
                UsernameNotFoundException("User details not found for the user: " + username));
        List<GrantedAuthority> authorities = employee.getAuthorities().stream().map(authority -> new
                        SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
        return new User(employee.getEmail(), employee.getPwd(), authorities);
    }
}
