package edu.wgu.android.studentscheduler.domain.course;

import java.io.Serializable;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.PlanComponent;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Course implements Serializable, PlanComponent {

    public Course(Long id, String courseName, String courseCode, String startDate, String endDate, CourseStatus status) {
        this.id = id;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    private Long id;
    private String courseName;
    private String courseCode;
    private String startDate;
    private String endDate;
    private CourseStatus status;

    @EqualsAndHashCode.Exclude
    private CourseInstructor instructor;

    @Setter
    @EqualsAndHashCode.Exclude
    private List<Assessment> assessments;

    @Setter
    @EqualsAndHashCode.Exclude
    private List<String> courseNotes; //optional display field

    @Override
    public String getTitle() {
        return courseCode + " - " + courseName;
    }

    @Override
    public String getDates() {
        return startDate + " - " + endDate;
    }

    //TODO add functionality that allows the user to adjust list of assessments associated with course to their heart's desire

    //TODO add functionality allows user to add/delete course notes

    //TODO add a feature that allows one to share notes (e-mail or SMS)

    //TODO add alerts that will fire off when the app is not running triggered by the course (start and end) dates

}
