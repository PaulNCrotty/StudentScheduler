package edu.wgu.android.studentscheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AssessmentAttempt {

    private Float finalScore;
    private String assessmentDate;
    private AssessmentStatus status;

    /**
     * Default constructor used to created future assessments
     * (i.e. assessments without a score or a planned date)
     */
    public AssessmentAttempt() {
        this.status = AssessmentStatus.FUTURE_UNAPPROVED;
    }

    /**
     * Constructor to be used to assign an assessment date to a new assessment attempt.
     * Should only be invoked when the assessment attempt has been approved by the
     * course instructor.
     *
     * @param assessmentDate - the date on which the assessment is scheduled
     */
    public AssessmentAttempt(String assessmentDate) {
        this.assessmentDate = assessmentDate;
        this.status = AssessmentStatus.FUTURE_APPROVED;
    }

}
