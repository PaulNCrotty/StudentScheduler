package edu.wgu.android.studentscheduler.persistence.dao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DegreePlanDao {

    private long id;
    private String name;
    private String studentName;
    private long createdDate;
    private long modifiedDate;

    //TODO add unit tests
    public long compareAuditDates(DegreePlanDao comparator) {
        long thisDateToUse = this.modifiedDate == 0 ? createdDate : modifiedDate;
        long comparatorDateToUse = comparator.modifiedDate == 0 ? createdDate: modifiedDate;

        return thisDateToUse - comparatorDateToUse;
    }

}
