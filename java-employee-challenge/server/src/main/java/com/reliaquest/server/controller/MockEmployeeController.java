package com.reliaquest.server.controller;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class MockEmployeeController {

    private final MockEmployeeService mockEmployeeService;

    @GetMapping()
    public ResponseEntity<List<MockEmployee>> getAllEmployees() {
        return ResponseEntity.ok(mockEmployeeService.getAllEmployees());
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<MockEmployee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.ok(mockEmployeeService.getEmployeesByNameSearch(searchString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MockEmployee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(mockEmployeeService.getEmployeeById(id));
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(mockEmployeeService.getHighestSalaryOfEmployees());
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(mockEmployeeService.getTop10HighestEarningEmployeeNames());
    }

    @PostMapping()
    public ResponseEntity<MockEmployee> createEmployee(@Valid @RequestBody CreateMockEmployeeInput input) {
        try {
            MockEmployee createdEmployee = mockEmployeeService.create(input);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        try {
            String deletedEmployeeName = mockEmployeeService.deleteById(UUID.fromString(id));
            return ResponseEntity.ok("Deleted employee Name : " + deletedEmployeeName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
