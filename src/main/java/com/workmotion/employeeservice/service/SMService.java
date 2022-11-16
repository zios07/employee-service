package com.workmotion.employeeservice.service;

import com.workmotion.employeeservice.config.EmployeeStateChangeInterceptor;
import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;

import static com.workmotion.employeeservice.utils.Constants.EMPLOYEE_ID_MESSAGE_HEADER;

@Service
@RequiredArgsConstructor
public class SMService {

    private final StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
    private final EmployeeStateChangeInterceptor stateMachineInterceptor;
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

    protected Flux<Collection<EmployeeState>> sendEventToStateMachine(Long employeeId, EmployeeEvent event) {
        Message<EmployeeEvent> eventMessage =
                MessageBuilder
                        .withPayload(event)
                        .build();
        return getMachine(employeeId)
                .flatMapMany(sm -> sm.sendEvent(Mono.just(eventMessage)))
                .map(result -> result.getRegion().getState().getIds());
    }

}
