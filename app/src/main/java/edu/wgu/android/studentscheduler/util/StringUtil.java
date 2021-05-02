package edu.wgu.android.studentscheduler.util;

public class StringUtil {

    /**
     * Simple static util method to determine if a String contains 'significant' or 'meaningful' data.
     *
     * @param s - the String reference to test
     * @return a boolean indicating if the string contains any meaningful data (i.e. is not null
     *  or only empty spaces)
     */
    public static boolean isEmpty(String s) {
        boolean isEmpty = false;
        if (s == null || s.replaceAll(" ", "").equals("")) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public static String toStandardCase(String s) {
        if(!isEmpty(s)) {
            s = s.trim();  //warning: do not chain (without trimming s on both sides of the '+' operator)
            s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return s;
    }

}
