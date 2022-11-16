package com.workmotion.employeeservice.repository;

import com.workmotion.employeeservice.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
