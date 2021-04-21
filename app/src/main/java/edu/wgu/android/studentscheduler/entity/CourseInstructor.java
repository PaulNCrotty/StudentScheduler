package edu.wgu.android.studentscheduler.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity(tableName = "course_instructor")
public class CourseInstructor {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name="first_name")
    private String firstName;

    @ColumnInfo(name="last_name")
    private String lastName;

    @ColumnInfo(name="phone_number")
    private String phoneNumber;

    private String email;

    @Embedded
    private Address address;

}

