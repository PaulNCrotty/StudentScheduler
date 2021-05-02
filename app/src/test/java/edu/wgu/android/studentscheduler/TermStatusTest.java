package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import edu.wgu.android.studentscheduler.domain.TermStatus;

public class TermStatusTest {

    @Test
    public void test__fromStatus__withPlanned() {
        String s ="Planned";
        Assert.assertEquals(TermStatus.FUTURE_UNAPPROVED, TermStatus.fromStatus(s));
    }

    @Test
    public void test__fromStatus__withPlannedLowerCased() {
        String s ="planned";
        Assert.assertEquals(TermStatus.FUTURE_UNAPPROVED, TermStatus.fromStatus(s));
    }

    @Test
    public void test__fromStatus__withPlannedUpperCased() {
        String s ="PLANNED";
        Assert.assertEquals(TermStatus.FUTURE_UNAPPROVED, TermStatus.fromStatus(s));
    }

    @Test
    public void test__fromStatus__withPlannedWonkyCased() {
        String s ="PLanNeD";
        Assert.assertEquals(TermStatus.FUTURE_UNAPPROVED, TermStatus.fromStatus(s));
    }

    @Test
    public void test__fromStatus__withPlans_returnsNull() {
        String s ="Plans";
        Assert.assertNull(TermStatus.fromStatus(s));
    }
}
