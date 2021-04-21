package edu.wgu.android.studentscheduler.entity;

public enum AssessmentType {

    PERFORMANCE("Performance"),
    OBJECTIVE("Objective");

    private String type;

    private AssessmentType(String type) {
        this.type = type;
    }

}
