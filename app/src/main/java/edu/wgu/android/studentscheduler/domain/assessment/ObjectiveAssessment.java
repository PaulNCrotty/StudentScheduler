package edu.wgu.android.studentscheduler.domain.assessment;

public class ObjectiveAssessment extends Assessment {

    private Float cutoffScore;

    public ObjectiveAssessment(Long id, String name, String code, Float cutoffScore) {
        super(id, name, code);
        this.cutoffScore = cutoffScore;
    }

}
