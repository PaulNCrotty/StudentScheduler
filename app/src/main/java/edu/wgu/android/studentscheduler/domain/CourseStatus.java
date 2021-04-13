package edu.wgu.android.studentscheduler.domain;

public enum CourseStatus {

    PLANNED("Planned"),
    DROPPED("Dropped"),
    ENROLLED("Enrolled"),
    IN_PROGRESS("In Progress"),
    PASSED("Passed"),
    FAILED("Failed");

    private String status;

    private CourseStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
