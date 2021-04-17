package edu.wgu.android.studentscheduler.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DegreePlan {

    private long id;
    private String name;
    private List<Term> terms;

}
