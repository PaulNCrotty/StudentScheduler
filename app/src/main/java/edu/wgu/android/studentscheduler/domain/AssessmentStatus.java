package edu.wgu.android.studentscheduler.domain;

public enum AssessmentStatus {

    FUTURE_UNAPPROVED("Planned"),
    FUTURE_APPROVED("Approved"),
    SCHEDULED("Scheduled"),
    PASSED("Passed"),
    FAILED("Failed");

    private String status;

    private AssessmentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
