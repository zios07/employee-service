package com.workmotion.employeeservice.config;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class StateMachinePersistenceConfig {

    @Bean
    public StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<EmployeeState, EmployeeEvent> stateMachineService(
            StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory,
            StateMachinePersist<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }

}
