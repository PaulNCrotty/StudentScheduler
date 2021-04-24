package edu.wgu.android.studentscheduler.domain.course;

import edu.wgu.android.studentscheduler.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseInstructor {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Address address;

}

