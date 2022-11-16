package com.workmotion.employeeservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Collection<EmployeeState> states;

    public Employee(String fullName, Collection<EmployeeState> states) {
        this.fullName = fullName;
        this.states = states;
    }

}
