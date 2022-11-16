package com.workmotion.employeeservice.config;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.region.RegionExecutionPolicy;

import static com.workmotion.employeeservice.utils.Constants.SECURITY_CHECK_REGION;
import static com.workmotion.employeeservice.utils.Constants.WORK_PERMIT_CHECK_REGION;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {

    private final StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister;

    // common config
    @Override
    public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config)
            throws Exception {
        config
            .withConfiguration()
            .autoStartup(true)
            .regionExecutionPolicy(RegionExecutionPolicy.PARALLEL);

        config.withPersistence()
            .runtimePersister(stateMachineRuntimePersister);
    }

    // states config
    @Override
    public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
        states
                .withStates()
                    .initial(EmployeeState.ADDED)
                    .fork(EmployeeState.FORK)
                    .state(EmployeeState.IN_CHECK)
                    .join(EmployeeState.JOIN)
                    .state(EmployeeState.APPROVED)
                    .state(EmployeeState.ACTIVE)
                .and()
                .withStates()
                    .parent(EmployeeState.IN_CHECK)
                    .region(WORK_PERMIT_CHECK_REGION)
                    .initial(EmployeeState.WORK_PERMIT_CHECK_STARTED)
                    .state(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .end(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
                .and()
                .withStates()
                    .parent(EmployeeState.IN_CHECK)
                    .region(SECURITY_CHECK_REGION)
                    .initial(EmployeeState.SECURITY_CHECK_STARTED)
                    .end(EmployeeState.SECURITY_CHECK_FINISHED);

    }

    // transitions config
    @Override
    public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions)
            throws Exception {

        transitions
                .withExternal()
                    .source(EmployeeState.ADDED)
                    .target(EmployeeState.FORK)
                    .event(EmployeeEvent.BEGIN_CHECK)
                .and()
                .withFork()
                    .source(EmployeeState.FORK)
                    .target(EmployeeState.IN_CHECK)
                .and()
                .withExternal()
                    .source(EmployeeState.WORK_PERMIT_CHECK_STARTED)
                    .target(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .event(EmployeeEvent.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
                .and()
                .withExternal()
                    .source(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .target(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
                    .event(EmployeeEvent.FINISH_WORK_PERMIT_CHECK)
                .and()
                .withExternal()
                    .source(EmployeeState.SECURITY_CHECK_STARTED)
                    .target(EmployeeState.SECURITY_CHECK_FINISHED)
                    .event(EmployeeEvent.FINISH_SECURITY_CHECK)
                .and()
                .withJoin()
                    .source(EmployeeState.SECURITY_CHECK_FINISHED)
                    .source(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
                    .target(EmployeeState.JOIN)
                .and()
                .withExternal()
                    .source(EmployeeState.JOIN)
                    .target(EmployeeState.APPROVED)
                .and()
                .withExternal()
                    .source(EmployeeState.APPROVED)
                    .target(EmployeeState.ACTIVE)
                    .event(EmployeeEvent.ACTIVATE);
    }

}
