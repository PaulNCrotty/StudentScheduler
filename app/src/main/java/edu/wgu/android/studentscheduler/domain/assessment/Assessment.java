package edu.wgu.android.studentscheduler.domain.assessment;


import java.io.Serializable;

import edu.wgu.android.studentscheduler.domain.PlanComponent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Assessment implements Serializable, PlanComponent {

    @Setter
    private Long id;
    private String name;
    private String code;
    private String assessmentDate;
    private AssessmentType type;

    @Override
    public String getTitle() {
        return code + " - " + name;
    }

    @Override
    public String getDates() {
        return assessmentDate;
    }

}
