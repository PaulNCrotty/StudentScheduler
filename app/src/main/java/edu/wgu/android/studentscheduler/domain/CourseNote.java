package edu.wgu.android.studentscheduler.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CourseNote  implements Serializable {

    @EqualsAndHashCode.Exclude
    long id;

    String title;
    String noteBody;

    @EqualsAndHashCode.Exclude
    String createdDate;

    @EqualsAndHashCode.Exclude
    String modifiedDate;

}
