package com.workmotion.employeeservice.controller;

import com.workmotion.employeeservice.domain.EmployeeEvent;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.dto.EmployeeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testGetEmployeeDetails() {

        EmployeeDTO employee = getEmployeeDTO(1L);
        assertTrue(employee.getStates().size() == 1);
        assertTrue(employee.getStates().contains(EmployeeState.ADDED));
    }

    @Test
    void testCreateEmployee() {

        EmployeeDTO employee = createEmployee();
        assertTrue(employee.getStates().size() == 1);
        assertTrue(employee.getStates().contains(EmployeeState.ADDED));
    }

    @Test
    void testBeginEmployeeCheck() {

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/employees/{employeeId}/{event}")
                        .build(1L, EmployeeEvent.BEGIN_CHECK))
                .exchange()
                .expectStatus().isOk();

        EmployeeDTO employee = getEmployeeDTO(1L);
        System.out.println("testBeginEmployeeCheck ::: ");
        System.out.println(employee.getStates());
        assertTrue(employee.getStates().contains(EmployeeState.IN_CHECK));
        assertTrue(employee.getStates().contains(EmployeeState.SECURITY_CHECK_STARTED));
        assertTrue(employee.getStates().contains(EmployeeState.WORK_PERMIT_CHECK_STARTED));
    }

    private EmployeeDTO getEmployeeDTO(long employeeId) {
        EntityExchangeResult<EmployeeDTO> exchangeResult = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/employees/{employeeId}")
                        .build(employeeId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDTO.class)
                .returnResult();

        return exchangeResult.getResponseBody();
    }

    private EmployeeDTO createEmployee() {
        EmployeeDTO body = new EmployeeDTO();
        body.setFullName("Test User");

        EntityExchangeResult<EmployeeDTO> exchangeResult = webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/employees")
                        .build())
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDTO.class)
                .returnResult();

        return exchangeResult.getResponseBody();
    }


}
