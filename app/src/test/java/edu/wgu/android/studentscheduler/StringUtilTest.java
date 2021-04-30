package edu.wgu.android.studentscheduler;


import org.junit.Assert;
import org.junit.Test;

import edu.wgu.android.studentscheduler.util.StringUtil;

public class StringUtilTest {

    @Test
    public void test__isEmpty__withNull() {
        Assert.assertTrue(StringUtil.isEmpty(null));
    }

    @Test
    public void test__isEmpty__withEmptyString() {
        Assert.assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    public void test__isEmpty__withStringOnlyContainingSpaces() {
        Assert.assertTrue(StringUtil.isEmpty("   "));
    }

    @Test
    public void test__isEmpty__withStringWithCharacters() {
        Assert.assertFalse(StringUtil.isEmpty("I'm a little teapot"));
    }

}
