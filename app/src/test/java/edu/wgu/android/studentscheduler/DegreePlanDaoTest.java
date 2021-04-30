package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void test__sortingDAOs__withCustomComparator() {
        long nullDate = 0;
        long now = getSecondsSinceEpoch();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 7);
        long sevenHoursInFuture = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        calendar.add(Calendar.MONTH, 7);
        long sevenMonthsPlusInFuture = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        calendar.add(Calendar.YEAR, -1);
        long fiveMonthsInPast = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        calendar.add(Calendar.MONTH, -7);
        long aYearAgo = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        calendar.add(Calendar.MONTH, 24);
        long aYearInTheFuture = calendar.getTimeInMillis()/MILLISECONDS_PER_SECOND;

        DegreePlanDao dao1 = new DegreePlanDao(1L, "dao1-plan", "dao1-student", nullDate, nullDate);
        DegreePlanDao dao2 = new DegreePlanDao(2L, "dao2-plan", "dao2-student", nullDate, nullDate);
        DegreePlanDao dao3 = new DegreePlanDao(3L, "dao3-plan", "dao3-student", nullDate, now);
        DegreePlanDao dao4 = new DegreePlanDao(4L, "dao4-plan", "dao4-student", nullDate, sevenHoursInFuture);
        DegreePlanDao dao5 = new DegreePlanDao(5L, "dao5-plan", "dao5-student", nullDate, fiveMonthsInPast);
        DegreePlanDao dao6 = new DegreePlanDao(6L, "dao6-plan", "dao6-student", fiveMonthsInPast, now);
        DegreePlanDao dao7 = new DegreePlanDao(7L, "dao7-plan", "dao7-student", aYearAgo, fiveMonthsInPast);
        DegreePlanDao dao8 = new DegreePlanDao(8L, "dao8-plan", "dao8-student", now, sevenHoursInFuture);
        DegreePlanDao dao9 = new DegreePlanDao(9L, "dao9-plan", "dao9-student", sevenHoursInFuture, sevenMonthsPlusInFuture);
        DegreePlanDao dao10 = new DegreePlanDao(10L, "dao10-plan", "dao10-student", aYearAgo, nullDate);
        DegreePlanDao dao11 = new DegreePlanDao(11L, "dao11-plan", "dao11-student", fiveMonthsInPast, nullDate);
        DegreePlanDao dao12 = new DegreePlanDao(12L, "dao12-plan", "dao12-student", now, nullDate);
        DegreePlanDao dao13 = new DegreePlanDao(13L, "dao13-plan", "dao13-student", sevenHoursInFuture, nullDate);
        DegreePlanDao dao14 = new DegreePlanDao(14L, "dao14-plan", "dao14-student", sevenMonthsPlusInFuture, nullDate);
        DegreePlanDao dao15 = new DegreePlanDao(15L, "dao15-plan", "dao15-student", sevenMonthsPlusInFuture, aYearInTheFuture);

        List<DegreePlanDao> unsortedDAOs = Arrays.asList(dao1, dao2, dao3, dao4, dao5, dao6, dao7, dao8, dao9, dao10, dao11, dao12, dao13, dao14, dao15);

        Collections.sort(unsortedDAOs, new DegreePlanDao.AuditDateComparator());

        List<Long> smallestAuditDateIds = Arrays.asList(1L, 2L);
        Assert.assertTrue(smallestAuditDateIds.contains(unsortedDAOs.get(0).getId()));
        Assert.assertTrue(smallestAuditDateIds.contains(unsortedDAOs.get(1).getId()));
        Assert.assertEquals(10L, unsortedDAOs.get(2).getId());

        List<Long> largerAuditDateIds = Arrays.asList(9L, 14L);
        Assert.assertTrue(largerAuditDateIds.contains(unsortedDAOs.get(unsortedDAOs.size() - 2).getId()));
        Assert.assertTrue(largerAuditDateIds.contains(unsortedDAOs.get(unsortedDAOs.size() - 3).getId()));
        Assert.assertEquals(15L, unsortedDAOs.get(unsortedDAOs.size() - 1).getId());

        List<Long> midRangeAuditDates = Arrays.asList(4L, 8L, 13L);
        Assert.assertTrue(midRangeAuditDates.contains(unsortedDAOs.get(unsortedDAOs.size() - 4).getId()));
        Assert.assertTrue(midRangeAuditDates.contains(unsortedDAOs.get(unsortedDAOs.size() - 5).getId()));
        Assert.assertTrue(midRangeAuditDates.contains(unsortedDAOs.get(unsortedDAOs.size() - 6).getId()));

        Collections.reverse(unsortedDAOs);
        System.out.println(unsortedDAOs);
    }

}
