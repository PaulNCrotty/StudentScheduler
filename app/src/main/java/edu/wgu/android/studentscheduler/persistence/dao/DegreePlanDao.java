package edu.wgu.android.studentscheduler.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class DegreePlanDao {

    private long id;
    private String name;
    private String studentName;
    private long createdDate;
    private long modifiedDate;

    //TODO add unit tests

    /**
     * Based upon the assumption that modified dates should always be greater than or equal to the
     * created date of an entity (in this case, a degree plan entity).
     *
     * @param comparator a degree plan entity or dao against which to compare recent db activity.
     * @return a long value: greater than zero if this entity has been created or modified
     * later than the comparator; 0 if they two entities share the same dates of activity; a value
     * less than zero if the comparator entity was actively worked on after this entity.
     */
    public long compareAuditDates(DegreePlanDao comparator) {
        long thisDateToUse = this.modifiedDate == 0 ? this.createdDate : this.modifiedDate;
        long comparatorDateToUse = comparator.modifiedDate == 0 ? comparator.createdDate: comparator.modifiedDate;

        return thisDateToUse - comparatorDateToUse;
    }

}
