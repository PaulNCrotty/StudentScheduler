package edu.wgu.android.studentscheduler.domain;

public enum TermStatus {

    PAST_COMPLETE("Completed"),
    PAST_INCOMPLETE("Incomplete"),
    CURRENT("Enrolled"),
    FUTURE_APPROVED("Approved"),
    FUTURE_UNAPPROVED("Planned"),
    FUTURE_EMPTY("New");


    private TermStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getStatus() {
        return status;
    }

}
