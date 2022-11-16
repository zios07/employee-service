package com.workmotion.employeeservice.dto;

import com.workmotion.employeeservice.domain.EmployeeState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;
    private String fullName;
    private Collection<EmployeeState> states;
}
