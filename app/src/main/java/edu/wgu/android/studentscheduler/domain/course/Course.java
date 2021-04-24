package edu.wgu.android.studentscheduler.domain.course;

import java.util.List;

import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Course {

    private Long id;
    private String courseName;
    private String courseCode;
    private List<Assessment> assessments;

    //TODO add functionality that allows the user to adjust list of assessments associated with course to their heart's desire

    //TODO add functionality that allows the user to edit course information

    //TODO add functionality allows user to add/delete course notes

    //TODO add detailed view of individual courses

    //TODO add a feature that allows one to share notes (e-mail or SMS)

    //TODO add alerts that will fire off when the app is not running triggered by the course (start and end) dates

}
