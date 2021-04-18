package edu.wgu.android.studentscheduler.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Assessment {

    private String name;
    private String code;
    private AssessmentType type;
    private List<AssessmentAttempt> attempts;

}
