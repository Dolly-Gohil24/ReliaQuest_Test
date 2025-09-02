package com.reliaquest.api.service;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.DeleteMockEmployeeInput;

import net.datafaker.Faker;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final Faker faker;

    @Getter
    private final List<Employee> employees;

    public List<Employee> getAllEmployees() {
        log.debug("Fetching all employees. Total count: {}", employees.size());
        return List.copyOf(employees);
    }

    public Optional<Employee> findById(@NonNull UUID uuid) {
        return employees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .findFirst();
    }

    public Employee create(@NonNull CreateEmployeeInput input) {
        final var mockEmployee = Employee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        employees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull DeleteMockEmployeeInput input) {
        final var mockEmployee = employees.stream()
                .filter(employee -> Objects.nonNull(employee.getName())
                        && employee.getName().equalsIgnoreCase(input.getName()))
                .findFirst();
        if (mockEmployee.isPresent()) {
            employees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }

        return false;
    }

    public List<Employee> getEmployeesByNameSearch(String nameFragment) {
        log.debug("Searching for employees with name containing: {}", nameFragment);
        List<Employee> filteredEmployees = employees.stream()
                .filter(employee -> employee.getName() != null
                        && employee.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .toList();

        log.debug("Found {} employees matching the search criteria.", filteredEmployees.size());
        return filteredEmployees;
    }

    public Employee getEmployeeById(String id) {
        log.debug("Fetching employee with ID: {}", id);
        return findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " not found."));
    }

    public Integer getHighestSalaryOfEmployees() {
        log.debug("Getting highest salary among employees.");
        Integer highestSalary = employees.stream()
                .filter(employee -> Objects.nonNull(employee.getSalary()))
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
        log.debug("Highest salary found: {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        log.debug("Fetching top 10 highest earning employee names.");
        List<String> employeeName = employees.stream()
                .filter(employee -> Objects.nonNull(employee.getSalary()))
                .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .toList();
        log.debug("Top 10 highest earning employees: {}", employeeName);
        return employeeName;
    }

    public String deleteById(@NonNull UUID uuid) {
        final var mockEmployee = findById(uuid);
        if (mockEmployee.isPresent()) {
            employees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return mockEmployee.get().getName();
        } else {
            throw new IllegalArgumentException("Employee with ID " + uuid + " not found.");
        }
    }
}
