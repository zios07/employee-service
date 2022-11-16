package com.workmotion.employeeservice.mapper;

import com.workmotion.employeeservice.domain.Employee;
import com.workmotion.employeeservice.dto.EmployeeDTO;

public class EmployeeMapper {

    public Employee employeeDtoToEmployee(EmployeeDTO employeeDTO) {
        return new Employee(employeeDTO.getId(), employeeDTO.getFullName(), employeeDTO.getStates());
    }

    public EmployeeDTO employeeToEmployeeDto(Employee employee) {
        return new EmployeeDTO(employee.getId(), employee.getFullName(), employee.getStates());
    }

}
