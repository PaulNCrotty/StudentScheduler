package edu.wgu.android.studentscheduler.domain.assessment;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Assessment implements Serializable {

    @Setter
    private Long id;
    private String name;
    private String code;
    private String assessmentDate;
    private AssessmentType type;

}
