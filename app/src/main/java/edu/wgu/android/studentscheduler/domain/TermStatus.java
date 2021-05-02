package edu.wgu.android.studentscheduler.domain;

import lombok.Getter;

@Getter
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

    public static TermStatus fromStatus(String status) {
        TermStatus termStatus = null;
        for(TermStatus s : values()) {
            if(s.getStatus().toLowerCase().equals(status.toLowerCase())) {
                termStatus = s;
                break;
            }
        }
        return termStatus;
    }
}
