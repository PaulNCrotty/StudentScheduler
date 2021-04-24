package edu.wgu.android.studentscheduler.domain.course;

public enum CourseStatus {

    PLANNED("Planned"), //orange play button
    DROPPED("Dropped"), //red_orange stop button
    ENROLLED("Enrolled"), //green stop button
    IN_PROGRESS("In Progress"), //green play button
    PASSED("Passed"), //dark green check mark?
    FAILED("Failed"); //red x circle

    private String status;

    private CourseStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
