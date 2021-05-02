package edu.wgu.android.studentscheduler.domain.assessment;

import lombok.Getter;

@Getter
public enum AssessmentType {

    PERFORMANCE("Performance"),
    OBJECTIVE("Objective");

    private String type;

    private AssessmentType(String type) {
        this.type = type;
    }

    public static AssessmentType fromType(String type) {
        AssessmentType AssessmentType = null;
        for(AssessmentType t : values()) {
            if(t.getType().toLowerCase().equals(type.toLowerCase())) {
                AssessmentType = t;
                break;
            }
        }
        return AssessmentType;
    }
}
