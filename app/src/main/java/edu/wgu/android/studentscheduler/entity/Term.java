package edu.wgu.android.studentscheduler.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Term {

    private String termName;
    private String startDate;
    private String endDate;
    private List<Course> courses;
    private TermStatus status;

    //TODO add functionality that allows the user to enter term titles, start dates, and end dates

    //TODO add functionality that allows the user to add as many terms as desired

    //TODO add functionality that allows the user to add as many courses as desired to a term

    //TODO add views: one term detailed view (with name, start date and end date)

    //TODO add functionality that validates changes to terms (e.g. cannot be deleted if courses are still assigned to it)



}
