package edu.wgu.android.studentscheduler.domain.course;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CourseInstructor implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

}

