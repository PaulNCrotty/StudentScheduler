package edu.wgu.android.studentscheduler.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Course {

    private String courseName;
    private String courseCode;
    private String startDate;
    private String endDate;
    private List<Assessment> assessments;
    private CourseStatus status;
    private CourseInstructor instructor;
    private List<String> courseNotes;


    //TODO add functionality that allows the user to adjust list of assessments associated with course to their heart's desire

    //TODO add functionality that allows the user to edit course information

    //TODO add functionality allows user to add/delete course notes

    //TODO add detailed view of individual courses

    //TODO add a feature that allows one to share notes (e-mail or SMS)

    //TODO add alerts that will fire off when the app is not running triggered by the course (start and end) dates

}
