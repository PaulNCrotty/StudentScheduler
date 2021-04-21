package edu.wgu.android.studentscheduler.entity;

public enum TermStatus {

    FUTURE_EMPTY("New"),
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
