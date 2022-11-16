package com.workmotion.employeeservice.controller;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.dto.EmployeeDTO;
import com.workmotion.employeeservice.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
@RequestMapping(value = "employees")
@RequiredArgsConstructor
@Api(value = "Employee API", tags = {"Employee resource"})
public class EmployeeController {

    private final EmployeeService employeeService;

    @ApiOperation(value = "Get employee details ", response = EmployeeDTO.class, tags = "Employee resource")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 404, message = "not found!!!")})

    @GetMapping(value = "{employeeId}")
    public Mono<EmployeeDTO> getEmployee(@PathVariable Long employeeId) throws Exception {
        return employeeService.getEmployeeDTO(employeeId);
    }

    @ApiOperation(value = "Add an employee", response = EmployeeDTO.class, tags = "Employee resource")
    @PostMapping
    public Mono<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) {
        return employeeService.createEmployee(employee);
    }

    @ApiOperation(value = "Change the state of employee by triggering an event", response = EmployeeState[].class, tags = "Employee resource")
    @PutMapping(value = "{employeeId}/{event}")
    public Flux<Collection<EmployeeState>> changeEmployeeState(@PathVariable Long employeeId, @PathVariable EmployeeEvent event) throws Exception {
        return employeeService.changeEmployeeState(employeeId, event);
    }

}
