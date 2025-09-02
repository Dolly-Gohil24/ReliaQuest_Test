package com.reliaquest.server.service;

import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmployeeService {

    private final Faker faker;

    @Getter
    private final List<MockEmployee> mockEmployees;

    public List<MockEmployee> getAllEmployees() {
        log.debug("Fetching all employees. Total count: {}", mockEmployees.size());
        return List.copyOf(mockEmployees);
    }

    public Optional<MockEmployee> findById(@NonNull UUID uuid) {
        return mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .findFirst();
    }

    public MockEmployee create(@NonNull CreateMockEmployeeInput input) {
        final var mockEmployee = MockEmployee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        mockEmployees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull DeleteMockEmployeeInput input) {
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getName())
                        && employee.getName().equalsIgnoreCase(input.getName()))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }

        return false;
    }

    public List<MockEmployee> getEmployeesByNameSearch(String nameFragment) {
        log.debug("Searching for employees with name containing: {}", nameFragment);
        List<MockEmployee> filteredEmployees = mockEmployees.stream()
                .filter(employee -> employee.getName() != null
                        && employee.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .toList();

        log.debug("Found {} employees matching the search criteria.", filteredEmployees.size());
        return filteredEmployees;
    }

    public MockEmployee getEmployeeById(String id) {
        log.debug("Fetching employee with ID: {}", id);
        return findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " not found."));
    }

    public Integer getHighestSalaryOfEmployees() {
        log.debug("Getting highest salary among employees.");
        Integer highestSalary = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getSalary()))
                .mapToInt(MockEmployee::getSalary)
                .max()
                .orElse(0);
        log.debug("Highest salary found: {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        log.debug("Fetching top 10 highest earning employee names.");
        List<String> employeeName = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getSalary()))
                .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                .limit(10)
                .map(MockEmployee::getName)
                .toList();
        log.debug("Top 10 highest earning employees: {}", employeeName);
        return employeeName;
    }

    public String deleteById(@NonNull UUID uuid) {
        final var mockEmployee = findById(uuid);
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return mockEmployee.get().getName();
        } else {
            throw new IllegalArgumentException("Employee with ID " + uuid + " not found.");
        }
    }
}
