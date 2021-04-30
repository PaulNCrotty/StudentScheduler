package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;

import static edu.wgu.android.studentscheduler.util.DateTimeUtil.MILLISECONDS_PER_SECOND;
import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getSecondsSinceEpoch;

public class DegreePlanDaoTest {

    /**
     *
     * compareAuditDates base test cases:
     *  NOTE: dates (longs) default to 0 with SQLiteHelper's implementation of Cursor; therefore, if
     *   a date DNE, it will become 0.
     *  NOTE: there are additional variations within these test cases. For example if both values exist, you can
     *  have a use case where one is bigger than the other, vice-versa, and where they are equal.
     *  Use equivalence partitioning and boundary principles to focus on the most interesting cases.
     *
     *  this: no dates (shouldn't happen in real life); comparator no dates (shouldn't happen in real life)
     *  this: no dates (shouldn't happen in real life); comparator created only
     *  this: no dates (shouldn't happen in real life); comparator modified only (shouldn't happen in real life)
     *  this: no dates (shouldn't happen in real life); comparator created and modified
     *
     *  this: created only; comparator no dates (shouldn't happen in real life)
     *  this: created only; comparator created only
     *  this: created only; comparator modified only (shouldn't happen in real life)
     *  this: created only; comparator created and modified
     *
     *  this: modified only (shouldn't happen in real life); comparator no dates (shouldn't happen in real life)
     *  this: modified only (shouldn't happen in real life); comparator created only
     *  this: modified only (shouldn't happen in real life); comparator modified only (shouldn't happen in real life)
     *  this: modified only (shouldn't happen in real life); comparator created and modified
     *
     *  this: created and modified; comparator no dates (shouldn't happen in real life)
     *  this: created and modified; comparator created only
     *  this: created and modified; comparator modified only (shouldn't happen in real life)
     *  this: created and modified; comparator created and modified
     *
     *  Further note that these tests verify the behavior we want: an entity without audit dates
     *  is counted as having an undefined history, and is not to be included in the candidates for
     *  the 'most recently' worked on degree plans.
     *
     */
    @Test
    public void test__comparedAuditDates__withThisNoDates_and_comparatorNoDates() {
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, 0);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, 0);

        Assert.assertEquals(0, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisNoDates_and_comparatorCreatedOnly() {
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, 0);
        long comparatorCreatedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, 0);

        Assert.assertEquals(-comparatorCreatedDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisNoDates_and_comparatorModifiedOnly() {
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, 0);
        long comparatorModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, comparatorModifiedDate);

        Assert.assertEquals(-comparatorModifiedDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisNoDates_and_comparatorBothDates() {
        long modifiedDate = getSecondsSinceEpoch();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long createdDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, 0);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", createdDate, modifiedDate);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) < 0);
        Assert.assertEquals(-modifiedDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisCreatedDate_and_comparatorNoDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long createdDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", createdDate, 0);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) > 0);
        Assert.assertEquals(createdDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisCreatedDate_and_comparatorCreatedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", thisCreatedDate, 0);
        long comparatorCreatedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) < 0);
        Assert.assertEquals((thisCreatedDate - comparatorCreatedDate),thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisCreatedDate_and_comparatorModifiedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long comparatorModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", thisCreatedDate, 0);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, comparatorModifiedDate);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) < 0);
        Assert.assertEquals((thisCreatedDate - comparatorModifiedDate), thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisCreatedDate_and_comparatorBothDates() {
        Calendar thisCalendar = Calendar.getInstance();
        thisCalendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisCreatedDate = thisCalendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long comparatorCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long modifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", thisCreatedDate, 0);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, modifiedDate);

        Assert.assertEquals((thisCreatedDate-modifiedDate), thisDao.compareAuditDates(comparatorDao));
    }



    @Test
    public void test__comparedAuditDates__withThisModifiedDate_and_comparatorNoDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long modifiedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, modifiedDate);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) > 0);
        Assert.assertEquals(modifiedDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisModifiedDate_and_comparatorCreatedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisModifiedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, thisModifiedDate);
        long comparatorCreatedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) < 0);
        Assert.assertEquals((thisModifiedDate - comparatorCreatedDate),thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisModifiedDate_and_comparatorModifiedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisModifiedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long comparatorModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, thisModifiedDate);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, comparatorModifiedDate);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) < 0);
        Assert.assertEquals((thisModifiedDate - comparatorModifiedDate), thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisModifiedDate_and_comparatorBothDates() {
        Calendar thisCalendar = Calendar.getInstance();
        thisCalendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisModifiedDate = thisCalendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long comparatorCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long modifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", 0, thisModifiedDate);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, modifiedDate);

        Assert.assertEquals((thisModifiedDate-modifiedDate), thisDao.compareAuditDates(comparatorDao));
    }



    @Test
    public void test__comparedAuditDates__withThisBothDates_and_comparatorNoDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long createdDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long modifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", createdDate, modifiedDate);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) > 0);
        Assert.assertEquals(modifiedDate, thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisBothDates_and_comparatorCreatedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long thisCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long thisModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", thisCreatedDate, thisModifiedDate);
        long comparatorCreatedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, 0);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) <= 0);
        Assert.assertEquals((thisModifiedDate - comparatorCreatedDate),thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisBothDates_and_comparatorModifiedOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        long createdDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long thisModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", createdDate, thisModifiedDate);
        long comparatorModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", 0, comparatorModifiedDate);

        Assert.assertTrue(thisDao.compareAuditDates(comparatorDao) <= 0);
        Assert.assertEquals((thisModifiedDate - comparatorModifiedDate), thisDao.compareAuditDates(comparatorDao));
    }

    @Test
    public void test__comparedAuditDates__withThisBothDates_and_comparatorBothDates() {
        Calendar thisCalendar = Calendar.getInstance();
        thisCalendar.add(Calendar.MONTH, -1);
        long thisCreatedDate = thisCalendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        thisCalendar.add(Calendar.DAY_OF_MONTH, 15);
        long thisModifiedDate = thisCalendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long comparatorCreatedDate = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;
        long comparatorModifiedDate = getSecondsSinceEpoch();
        DegreePlanDao thisDao = new DegreePlanDao(1L, "", "", thisCreatedDate, thisModifiedDate);
        DegreePlanDao comparatorDao = new DegreePlanDao(2L, "", "", comparatorCreatedDate, comparatorModifiedDate);

        Assert.assertEquals((thisModifiedDate-comparatorModifiedDate), thisDao.compareAuditDates(comparatorDao));
    }

}
