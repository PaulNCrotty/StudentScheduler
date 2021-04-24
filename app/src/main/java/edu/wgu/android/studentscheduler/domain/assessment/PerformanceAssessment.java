package edu.wgu.android.studentscheduler.domain.assessment;

import java.util.List;

public class PerformanceAssessment extends Assessment {

    private List<PerformanceTask> assessmentTasks;

    public PerformanceAssessment(Long id, String name, String code, List<PerformanceTask> assessmentTasks) {
        super(id, name, code);
        this.assessmentTasks = assessmentTasks;
    }

}
