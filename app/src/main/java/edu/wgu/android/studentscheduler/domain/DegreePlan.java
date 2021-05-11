package edu.wgu.android.studentscheduler.domain;

import java.util.List;

import edu.wgu.android.studentscheduler.domain.term.Term;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DegreePlan {

    private long id;
    private String name;
    private String studentName;
    private List<Term> terms;

 }
