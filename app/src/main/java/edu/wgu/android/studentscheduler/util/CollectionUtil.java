package edu.wgu.android.studentscheduler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {

    public static <E> List<E> copyAndAdd(Collection<E> a, Collection<E> b) {
        //capture any saved objects first
        boolean savedObjectsExist = a != null && a.size() > 0;
        List<E> toDisplay = savedObjectsExist ? new ArrayList<E>(a) : new ArrayList<>();

        //add any toBe objects next
        if(b != null && b.size() > 0) {
            toDisplay.addAll(b);
        }

        return toDisplay;
    }
}
