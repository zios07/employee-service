package com.workmotion.employeeservice.service;

import com.workmotion.employeeservice.domain.Employee;
import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.dto.EmployeeDTO;
import com.workmotion.employeeservice.exception.NotFoundException;
import com.workmotion.employeeservice.mapper.EmployeeMapper;
import com.workmotion.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper mapper = new EmployeeMapper();
    private final EmployeeRepository employeeRepository;
    private final SMService stateMachineService;

    public Mono<EmployeeDTO> createEmployee(EmployeeDTO employeeDto) {
        Employee employee = mapper.employeeDtoToEmployee(employeeDto);
        employee.setStates(List.of(EmployeeState.ADDED));
        employee = employeeRepository.save(employee);
        return Mono.just(mapper.employeeToEmployeeDto(employee));
    }

    public Mono<EmployeeDTO> getEmployeeDTO(Long employeeId) throws Exception {
        Employee employee = getEmployee(employeeId);
        return Mono.just(mapper.employeeToEmployeeDto(employee));
    }

    public Flux<Collection<EmployeeState>> changeEmployeeState(Long employeeId, EmployeeEvent event) {
        return stateMachineService.sendEventToStateMachine(employeeId, event);
    }

    private Employee getEmployee(Long employeeId) throws NotFoundException {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(String.format("Employee with id %s does not exist", employeeId)));
    }

}
