package edu.wgu.android.studentscheduler.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Term {

    private String termName;
    private String startDate;
    private String endDate;
    private List<Course> courseList;
    private TermStatus status;

}
