package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

import edu.wgu.android.studentscheduler.domain.TermStatus;
import edu.wgu.android.studentscheduler.persistence.MockDegreePlanRepository;

import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getDateString;
import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getDateTimeString;

public class MockDegreePlanRepositoryTest {

    private static final int MONTHS_PER_TERM = 6;
    private static final int AVG_YEARS_TO_GRADUATE = 5;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int TERMS_PER_YEAR = MONTHS_PER_YEAR/MONTHS_PER_TERM;

    private static final MockDegreePlanRepository dpRepo = new MockDegreePlanRepository();

    @Test
    public void test__getBeginningOfCurrentTerm() {
        Calendar lowerBoundary = Calendar.getInstance();
        lowerBoundary.add(Calendar.MONTH, -(MONTHS_PER_TERM - 1));
        MockDegreePlanRepository.atBeginningOfMonth(lowerBoundary);
        System.out.println("lowerBoundary: " + getDateString(lowerBoundary));

        Calendar upperBoundary = Calendar.getInstance();
        MockDegreePlanRepository.atBeginningOfMonth(upperBoundary);
        System.out.println("upperBoundary: " + getDateString(upperBoundary));

        for (int i = 0; i < 100; i++) {
            Calendar beginningOfCurrentTerm = MockDegreePlanRepository.getBeginningOfCurrentTerm();
            System.out.println();
            Assert.assertTrue(
                    lowerBoundary.compareTo(beginningOfCurrentTerm) <= 0 &&
                            upperBoundary.compareTo(beginningOfCurrentTerm) >= 0
            );
        }
    }

    @Test
    public void test__atBeginningOfMonth() {
        Calendar today = Calendar.getInstance();
        MockDegreePlanRepository.atBeginningOfMonth(today);

        System.out.println("The beginning of this month was " + getDateTimeString(today));
        Assert.assertSame(1, today.get(Calendar.DAY_OF_MONTH));
        Assert.assertSame(0, today.get(Calendar.HOUR_OF_DAY));
        Assert.assertSame(0, today.get(Calendar.MINUTE));
        Assert.assertSame(0, today.get(Calendar.SECOND));
        Assert.assertSame(0, today.get(Calendar.MILLISECOND));
    }

    @Test
    public void test__getTermStatus__withAnyPastDates() {
        Calendar termStartDate = Calendar.getInstance();
        termStartDate.add(Calendar.MONTH, -(MONTHS_PER_TERM));
        MockDegreePlanRepository.atBeginningOfMonth(termStartDate);

        for (int i = 0; i < 100; i++) {
            TermStatus status = dpRepo.getTermStatus(termStartDate);
            System.out.println("Status is " + status.getStatus());

            Assert.assertTrue(status == TermStatus.PAST_COMPLETE || status == TermStatus.PAST_INCOMPLETE);
        }
    }

    @Test
    public void test__getTermStatus__withPastTermDates() {
        for (int i = 0; i < 100; i++) {
            Calendar termStartDate = Calendar.getInstance();
            termStartDate.setTime(MockDegreePlanRepository.CURRENT_TERM_START.getTime());
            //increment terms by MONTHS_PER_TERM units
            termStartDate.add(Calendar.MONTH, MONTHS_PER_TERM * -(MockDegreePlanRepository.RANDOM.nextInt(TERMS_PER_YEAR * AVG_YEARS_TO_GRADUATE)+1));
            MockDegreePlanRepository.atBeginningOfMonth(termStartDate);
            TermStatus status = dpRepo.getTermStatus(termStartDate);
            System.out.println("Status is " + status.getStatus());

            Assert.assertTrue(status == TermStatus.PAST_COMPLETE || status == TermStatus.PAST_INCOMPLETE);
        }
    }

    @Test
    public void test__getTermStatus__withCurrentTermDate() {
        TermStatus status = dpRepo.getTermStatus(MockDegreePlanRepository.CURRENT_TERM_START);
        System.out.println("Status is " + status.getStatus());

        Assert.assertSame(TermStatus.CURRENT, status);
    }

    @Test
    public void test__getTermStatus__withFutureStartDates() {
        Calendar lowerBoundary = Calendar.getInstance();
        lowerBoundary.add(Calendar.MONTH, 1);
        MockDegreePlanRepository.atBeginningOfMonth(lowerBoundary);
        System.out.println("lowerBoundary for approval: " + getDateString(lowerBoundary));

        Calendar upperBoundary = Calendar.getInstance();
        upperBoundary.add(Calendar.MONTH, MONTHS_PER_TERM);
        MockDegreePlanRepository.atBeginningOfMonth(upperBoundary);
        System.out.println("upperBoundary approval : " + getDateString(upperBoundary));

        for (int i = 0; i < 100; i++) {
            Calendar termStartDate = Calendar.getInstance();
            termStartDate.setTime(MockDegreePlanRepository.CURRENT_TERM_START.getTime());
            //increment terms by MONTHS_PER_TERM units
            termStartDate.add(Calendar.MONTH, MONTHS_PER_TERM * (MockDegreePlanRepository.RANDOM.nextInt(TERMS_PER_YEAR * AVG_YEARS_TO_GRADUATE)+1));
            MockDegreePlanRepository.atBeginningOfMonth(termStartDate);

            TermStatus status = dpRepo.getTermStatus(termStartDate);
            System.out.println("Status is " + status.getStatus());

            if(status == TermStatus.FUTURE_APPROVED) {
                Assert.assertTrue(termStartDate.compareTo(lowerBoundary) >= 0 &&
                        termStartDate.compareTo(upperBoundary) <= 0);
            } else {
                Assert.assertSame(TermStatus.FUTURE_UNAPPROVED, status);
            }
        }
    }

}
