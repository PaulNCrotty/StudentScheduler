package edu.wgu.android.studentscheduler.domain;

public enum TermStatus {

    FUTURE_UNAPPROVED("Planned"),
    FUTURE_APPROVED("Approved"),
    CURRENT("Enrolled"),
    PAST_INCOMPLETE("Incomplete"),
    PAST_COMPLETE("Completed");


    private TermStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getStatus() {
        return status;
    }

}
