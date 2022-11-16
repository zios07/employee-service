package com.workmotion.employeeservice;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StateMachineTests {

    @Autowired
    private StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;

    private StateMachine<EmployeeState, EmployeeEvent> stateMachine;

    @BeforeEach
    public void setup() {
        stateMachine = stateMachineFactory.getStateMachine("test-sm");
        stateMachine.stopReactively().block();
    }

    @Test
    public void testInitial() throws Exception {
        StateMachineTestPlan<EmployeeState, EmployeeEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    void testHappyPath() throws Exception {
        StateMachineTestPlan<EmployeeState, EmployeeEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and().step()
                        .sendEvent(EmployeeEvent.BEGIN_CHECK)
                        .expectStateChanged(3)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
                        .and().step()
                        .sendEvent(EmployeeEvent.FINISH_SECURITY_CHECK)
                        .expectStateChanged(1)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
                        .and().step()
                        .sendEvent(EmployeeEvent.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
                        .expectStateChanged(1)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                        .and().step()
                        .sendEvent(EmployeeEvent.FINISH_WORK_PERMIT_CHECK)
                        .expectStateChanged(2)
                        .expectState(EmployeeState.APPROVED)
                        .and().step()
                        .sendEvent(EmployeeEvent.ACTIVATE)
                        .expectStateChanged(1)
                        .expectState(EmployeeState.ACTIVE)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    void testNotAllowedTransition1() throws Exception {
        StateMachineTestPlan<EmployeeState, EmployeeEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and().step()
                        // Not allowed, state should not change
                        .sendEvent(EmployeeEvent.ACTIVATE)
                        .expectState(EmployeeState.ADDED)
                        .and()
                        .build();

        plan.test();
    }


    @Test
    void testNotAllowedTransition2() throws Exception {
        StateMachineTestPlan<EmployeeState, EmployeeEvent> plan =
                StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(EmployeeState.ADDED)
                        .and().step()
                        .sendEvent(EmployeeEvent.BEGIN_CHECK)
                        .expectStateChanged(3)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
                        .and().step()
                        .sendEvent(EmployeeEvent.FINISH_SECURITY_CHECK)
                        .expectStateChanged(1)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
                        .and().step()
//                        // Not allowed, state should not change
                        .sendEvent(EmployeeEvent.ACTIVATE)
                        .expectStateChanged(0)
                        .expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
                        .and()
                        .build();

        plan.test();
    }

}
