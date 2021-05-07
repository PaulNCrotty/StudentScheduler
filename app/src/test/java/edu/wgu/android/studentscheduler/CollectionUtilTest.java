package edu.wgu.android.studentscheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.wgu.android.studentscheduler.util.CollectionUtil.copyAndAdd;

public class CollectionUtilTest {

    /***
     * Use cases
     *
     * Saved objects are null && toBeObjects are null
     * Saved objects are null && toBeObjects are not null but empty
     * Saved objects are null && toBeObjects exist
     *
     * Saved objects are not null but empty && toBeObjects are null
     * Saved objects are not null but empty && toBeObjects are not null but empty
     * Saved objects are not null but empty && toBeObjects exist
     *
     * Saved objects exist && toBeObjects are null
     * Saved objects exist && toBeObjects are not null but empty
     * Saved objects exist && toBeObjects exist
     *
     */
    @Test
    public void test__getItemsToDisplay_withSavedObjects_null_and_toBeObjects_null() {
        List<String> savedObjects = null;
        List<String> toBeObjects = null;

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(0, toDisplay.size());
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_null_and_toBeObjects_empty() {
        List<String> savedObjects = null;
        List<String> toBeObjects = new ArrayList<>();

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(0, toDisplay.size());
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_null_and_toBeObjects_withValues() {
        List<String> savedObjects = null;
        List<String> toBeObjects = Arrays.asList("a", "b");

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(toBeObjects.size(), toDisplay.size());
        Assert.assertTrue(toDisplay.containsAll(toBeObjects));
    }



    @Test
    public void test__getItemsToDisplay_withSavedObjects_empty_and_toBeObjects_null() {
        List<String> savedObjects = new ArrayList<>();
        List<String> toBeObjects = null;

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(0, toDisplay.size());
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_empty_and_toBeObjects_empty() {
        List<String> savedObjects = new ArrayList<>();
        List<String> toBeObjects = new ArrayList<>();

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(0, toDisplay.size());
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_empty_and_toBeObjects_withValues() {
        List<String> savedObjects = new ArrayList<>();
        List<String> toBeObjects = Arrays.asList("a", "b");

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(toBeObjects.size(), toDisplay.size());
        Assert.assertTrue(toDisplay.containsAll(toBeObjects));
    }



    @Test
    public void test__getItemsToDisplay_withSavedObjects_withValues_and_toBeObjects_null() {
        List<String> savedObjects = Arrays.asList("c", "d");
        List<String> toBeObjects = null;

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(savedObjects.size(), toDisplay.size());
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_withValues_and_toBeObjects_empty() {
        List<String> savedObjects = Arrays.asList("c", "d");
        List<String> toBeObjects = new ArrayList<>();

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(savedObjects.size(), toDisplay.size());
        Assert.assertEquals(0, toBeObjects.size()); //verify toBe not mutated
    }

    @Test
    public void test__getItemsToDisplay_withSavedObjects_withValues_and_toBeObjects_withValues() {
        List<String> savedObjects = Arrays.asList("c", "d");
        List<String> toBeObjects = Arrays.asList("a", "b");

        int savedObjectsSize = savedObjects.size();
        int toBeObjectsSize = toBeObjects.size();
        int expectedSize = savedObjectsSize + toBeObjectsSize;

        List<String> toDisplay = copyAndAdd(savedObjects, toBeObjects);

        Assert.assertNotNull(toDisplay);
        Assert.assertEquals(expectedSize, toDisplay.size());
        Assert.assertTrue(toDisplay.containsAll(savedObjects));
        Assert.assertTrue(toDisplay.containsAll(toBeObjects));

        Assert.assertEquals(savedObjectsSize, savedObjects.size()); // verify saved objects not mutated
        Assert.assertEquals(toBeObjectsSize, toBeObjects.size()); // verify toBe objects not mutated

        //finally, verify saved and toBe are still mutually exclusive
        for(String s: savedObjects) {
            Assert.assertFalse(toBeObjects.contains(s));
        }
    }

}
