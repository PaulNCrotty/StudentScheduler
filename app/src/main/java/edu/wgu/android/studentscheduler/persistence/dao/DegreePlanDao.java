package edu.wgu.android.studentscheduler.persistence.dao;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DegreePlanDao {

    private long id;
    private String name;
    private String studentName;
    private long createdDate;
    private long modifiedDate;

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
        long thisDateToUse = getLastModified();
        long comparatorDateToUse = comparator.getLastModified();

        return thisDateToUse - comparatorDateToUse;
    }

    public long getLastModified() {
        return modifiedDate == 0 ? createdDate : modifiedDate;
    }

    public static class AuditDateComparator implements Comparator<DegreePlanDao> {

        @Override
        public int compare(DegreePlanDao o1, DegreePlanDao o2) {
            return (int) o1.compareAuditDates(o2);
        }
    }

}
