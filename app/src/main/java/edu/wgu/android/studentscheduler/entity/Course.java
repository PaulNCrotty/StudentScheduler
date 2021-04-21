package edu.wgu.android.studentscheduler.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Entity(tableName = "course",
        foreignKeys = {@ForeignKey(entity = Assessment.class, parentColumns = "id", childColumns = "assessment"),
                @ForeignKey(entity = CourseInstructor.class, parentColumns = "id", childColumns = "course_instructor")
})
public class Course {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name="course_name")
    private String courseName;

    @ColumnInfo(name="course_code")
    private String courseCode;

    @ColumnInfo(name="start_date")
    private String startDate;

    @ColumnInfo(name="end_date")
    private String endDate;

    @ColumnInfo(name="assessment")
    private List<Assessment> assessments;

    private CourseStatus status;

    private CourseInstructor instructor;

    @ColumnInfo(name="note")
    private List<String> courseNotes;


    //TODO add functionality that allows the user to adjust list of assessments associated with course to their heart's desire

    //TODO add functionality that allows the user to edit course information

    //TODO add functionality allows user to add/delete course notes

    //TODO add detailed view of individual courses

    //TODO add a feature that allows one to share notes (e-mail or SMS)

    //TODO add alerts that will fire off when the app is not running triggered by the course (start and end) dates

}
