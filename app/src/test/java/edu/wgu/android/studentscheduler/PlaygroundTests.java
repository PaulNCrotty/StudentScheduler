package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Not true unit tests... just a sandbox to verify different Java behaviors and some other assumptions
 * Other classes in this package contain valid tests (tests that truly exercise the code)
 */
public class PlaygroundTests {

    /***
     * used verify (deep) copy behavior of ArrayList(Collection<? extends E> c) constructor
     */
    @Test
    public void test__arrayCopying() {
        List<String> a1 = Arrays.asList("a", "b", "c", "d");
        List<String> a2 = new ArrayList<>(a1);

        int initialA1Size = a1.size();
        int initialA2Size = a2.size();

        Assert.assertEquals(initialA1Size, initialA2Size);

        a2.add("e");

        Assert.assertEquals(initialA1Size, a1.size());
        Assert.assertEquals(initialA2Size + 1, a2.size());
        Assert.assertTrue(a2.contains("e"));
        Assert.assertFalse(a1.contains("e"));
    }

    @Test
    public void test__moreArrayCopying() {
        List<String> a1 = Arrays.asList("a", "b", "c", "d");
        List<String> a2 = Arrays.asList("e", "f", "g");

        int initialA1Size = a1.size();
        int initialA2Size = a2.size();

        List<String> a3 = new ArrayList<>(a1);
        a3.addAll(a2);

        Assert.assertEquals(initialA1Size, a1.size());
        Assert.assertEquals(initialA2Size, a2.size());
        Assert.assertEquals(initialA1Size + initialA2Size, a3.size());

        // verify that a3 is a superset of both a1 and a2
        Assert.assertTrue(a3.containsAll(a1));
        Assert.assertTrue(a3.containsAll(a2));

        // verify that a1 and a2 are still mutually exclusive sets
        for(String s: a1) {
            Assert.assertFalse(a2.contains(s));
        }
    }

    @Test
    public void test__nullArrayCopying() {
        List<String> a1 = Arrays.asList("a", "b", "c", "d");
        List<String> a2 = null;

        int initialA1Size = a1.size();

        List<String> a3 = new ArrayList<>(a1);
        // turns out null check is required (but why should I be surprised?)
        if(a2 != null) {
            a3.addAll(a2);
        }

        Assert.assertEquals(initialA1Size, a1.size());
        Assert.assertEquals(initialA1Size, a3.size());

        // verify that a3 is a superset of both a1 and a2
        Assert.assertTrue(a3.containsAll(a1));

    }
}
