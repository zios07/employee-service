package com.workmotion.employeeservice.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EmployeeState {

    ADDED,
    APPROVED,
    ACTIVE,
    SECURITY_CHECK_STARTED,
    SECURITY_CHECK_FINISHED,
    WORK_PERMIT_CHECK_STARTED,
    WORK_PERMIT_CHECK_PENDING_VERIFICATION,
    WORK_PERMIT_CHECK_FINISHED,
    IN_CHECK,
    FORK,
    JOIN;


}
