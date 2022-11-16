package com.workmotion.employeeservice;

import com.workmotion.employeeservice.domain.Employee;
import com.workmotion.employeeservice.domain.EmployeeState;
import com.workmotion.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackageClasses = EmployeeRepository.class)
@EntityScan(basePackageClasses = Employee.class)
public class EmployeeServiceApplication implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            var employee1 = new Employee();
            employee1.setFullName("User1");
            employee1.setStates(List.of(EmployeeState.ADDED));

            var employee2 = new Employee();
            employee2.setFullName("User2");
            employee2.setStates(List.of(EmployeeState.ADDED));
            employeeRepository.saveAll(List.of(employee1, employee2));
        }
    }
}
