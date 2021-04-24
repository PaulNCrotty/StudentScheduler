package edu.wgu.android.studentscheduler.domain.assessment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Assessment {

    private Long id;
    private String name;
    private String code;
    private String assessmentDate;
    private AssessmentType type;

}
