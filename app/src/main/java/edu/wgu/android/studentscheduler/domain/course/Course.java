package edu.wgu.android.studentscheduler.domain.course;

import java.io.Serializable;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Course implements Serializable {

    private Long id;
    private String courseName;
    private String courseCode;
    private String startDate;
    private String endDate;
    private CourseStatus status;
    @EqualsAndHashCode.Exclude
    private CourseInstructor instructor;
    @EqualsAndHashCode.Exclude
    private List<Assessment> assessments;
    @EqualsAndHashCode.Exclude
    private List<String> courseNotes; //optional display field

    //TODO add functionality that allows the user to adjust list of assessments associated with course to their heart's desire

    //TODO add functionality allows user to add/delete course notes

    //TODO add a feature that allows one to share notes (e-mail or SMS)

    //TODO add alerts that will fire off when the app is not running triggered by the course (start and end) dates

}
