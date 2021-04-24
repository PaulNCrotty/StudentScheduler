package edu.wgu.android.studentscheduler.domain.assessment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AssessmentAttempt {

    private Long id;
    private Float finalScore;
    private String attemptDate;
    private Assessment assessment;
    private AssessmentStatus status;    //transient field

    /**
     * Default constructor used to created future assessments
     * (i.e. assessments without a score or a planned date)
     */
    public AssessmentAttempt() {
        this.status = AssessmentStatus.FUTURE_UNAPPROVED;
    }

    /**
     * Constructor to be used to assign an attempt date to a new assessment attempt.
     * Should only be invoked when the assessment attempt has been approved by the
     * course instructor.
     *
     * @param attemptDate - the date on which the assessment is scheduled
     */
    public AssessmentAttempt(String attemptDate) {
        this.attemptDate = attemptDate;
        this.status = AssessmentStatus.FUTURE_APPROVED;
    }

}
