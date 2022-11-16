package com.workmotion.employeeservice.controller;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.dto.EmployeeDTO;
import com.workmotion.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequestMapping(value = "employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping(value = "{employeeId}")
    public Mono<EmployeeDTO> getEmployee(@PathVariable Long employeeId) throws Exception {
        return employeeService.getEmployeeDTO(employeeId);
    }

    @PostMapping
    public Mono<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping(value = "{employeeId}/{event}")
    public Flux<Collection<EmployeeState>> changeEmployeeState(@PathVariable Long employeeId, @PathVariable EmployeeEvent event) throws Exception {
        return employeeService.changeEmployeeState(employeeId, event);
    }

}
