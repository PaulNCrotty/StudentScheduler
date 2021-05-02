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

    @Test
    public void test__toStandardCase__withNull() {
        Assert.assertNull(StringUtil.toStandardCase(null));
    }

    @Test
    public void test__toStandardCase__withEmptyString() {
        Assert.assertEquals("", StringUtil.toStandardCase(""));
    }

    @Test
    public void test__toStandardCase__withNonEmptyString() {
        Assert.assertEquals("Hello world", StringUtil.toStandardCase("hello world"));
    }

    @Test
    public void test__toStandardCase__withNonEmptyStringContainingLeadingSpaces() {
        Assert.assertEquals("Hello world", StringUtil.toStandardCase("   hello world"));
    }

    @Test
    public void test__toStandardCase__withNonEmptyStringContainingLeadingSpacesAndOtherCapitalization() {
        Assert.assertEquals("Hello world", StringUtil.toStandardCase("   hello WORLD"));
    }

    /***
     * sanity check on integer division (should truncate any remainders and only have the bytes for the quotient)
     */
    @Test
    public void test__stuff() {
        Assert.assertEquals(0, 0/3);
        Assert.assertEquals(0, 1/3);
        Assert.assertEquals(0, 2/3);

        Assert.assertEquals(1, 3/3);
        Assert.assertEquals(1, 4/3);
        Assert.assertEquals(1, 5/3);

        Assert.assertEquals(2, 6/3);
        Assert.assertEquals(2, 7/3);
        Assert.assertEquals(2, 8/3);

        Assert.assertEquals(3, 9/3);
        Assert.assertEquals(3, 10/3);
        Assert.assertEquals(3, 11/3);
    }

}
