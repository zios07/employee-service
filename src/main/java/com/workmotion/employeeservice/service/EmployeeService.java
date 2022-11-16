package com.workmotion.employeeservice.service;

import com.workmotion.employeeservice.config.EmployeeStateChangeInterceptor;
import com.workmotion.employeeservice.domain.Employee;
import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.dto.EmployeeDTO;
import com.workmotion.employeeservice.exception.NotFoundException;
import com.workmotion.employeeservice.mapper.EmployeeMapper;
import com.workmotion.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;
import java.util.List;

import static com.workmotion.employeeservice.utils.Constants.EMPLOYEE_ID_MESSAGE_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper mapper = new EmployeeMapper();
    private final EmployeeRepository employeeRepository;
    private final StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
    private final EmployeeStateChangeInterceptor stateMachineInterceptor;

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
        return sendEventToStateMachine(employeeId, event);
    }

    private Employee getEmployee(Long employeeId) throws NotFoundException {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(String.format("Employee with id %s does not exist", employeeId)));
    }

    private Mono<StateMachine<EmployeeState, EmployeeEvent>> getMachine(Long employeeId) {
        return Mono.just(String.valueOf(employeeId))
                .publishOn(Schedulers.boundedElastic())
                .map(stateMachineService::acquireStateMachine)
                .map(sm ->
                        {
                            sm.getExtendedState().getVariables().put(EMPLOYEE_ID_MESSAGE_HEADER, employeeId);
                            sm.getStateMachineAccessor()
                                    .doWithAllRegions(sma -> sma.addStateMachineInterceptor(stateMachineInterceptor));
                            return sm;
                        }
                );
    }

    // TODO optimize return type?
    private Flux<Collection<EmployeeState>> sendEventToStateMachine(Long employeeId, EmployeeEvent event) {
        Message<EmployeeEvent> eventMessage =
                MessageBuilder
                        .withPayload(event)
                        .build();
        return getMachine(employeeId)
                .flatMapMany(sm -> sm.sendEvent(Mono.just(eventMessage)))
                .map(result -> result.getRegion().getState().getIds());
    }

}
