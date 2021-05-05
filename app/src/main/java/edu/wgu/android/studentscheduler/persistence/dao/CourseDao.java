package edu.wgu.android.studentscheduler.persistence.dao;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CourseDao {

    private long id;
    private long termId;
    private long instructorId;
    private String name;
    private String code;
    private long startDate;
    private long endDate;
    private String status;

}
