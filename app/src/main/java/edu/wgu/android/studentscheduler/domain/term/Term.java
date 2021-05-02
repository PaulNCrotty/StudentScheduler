package edu.wgu.android.studentscheduler.domain.term;

import java.util.List;

import edu.wgu.android.studentscheduler.domain.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Term {

    private Long id;
    private String termName;
    private String startDate;
    private String endDate;
    @Setter
    private List<Course> courses;
    private TermStatus status;

    //TODO add functionality that allows the user to enter term titles, start dates, and end dates

    //TODO add functionality that allows the user to add as many terms as desired

    //TODO add functionality that allows the user to add as many courses as desired to a term

    //TODO add views: one term detailed view (with name, start date and end date)

    //TODO add functionality that validates changes to terms (e.g. cannot be deleted if courses are still assigned to it)



}
