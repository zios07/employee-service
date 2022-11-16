package com.workmotion.employeeservice.config;

import com.workmotion.employeeservice.domain.Employee;
import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.exception.NotFoundException;
import com.workmotion.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import static com.workmotion.employeeservice.utils.Constants.EMPLOYEE_ID_MESSAGE_HEADER;

@Component
@RequiredArgsConstructor
public class EmployeeStateChangeInterceptor extends StateMachineInterceptorAdapter<EmployeeState, EmployeeEvent> {

    private final EmployeeRepository employeeRepository;

    @Override
    public void postStateChange(State<EmployeeState, EmployeeEvent> state,
                                Message<EmployeeEvent> message,
                                Transition<EmployeeState, EmployeeEvent> transition,
                                StateMachine<EmployeeState, EmployeeEvent> stateMachine,
                                StateMachine<EmployeeState, EmployeeEvent> rootStateMachine) {
        Long employeeId = stateMachine.getExtendedState().get(EMPLOYEE_ID_MESSAGE_HEADER, Long.class);
        if (employeeId != null) {
            try {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new NotFoundException(String.format("Employee with id %s does not exist", employeeId)));
                employee.setStates(rootStateMachine.getState().getIds());
                employeeRepository.save(employee);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
