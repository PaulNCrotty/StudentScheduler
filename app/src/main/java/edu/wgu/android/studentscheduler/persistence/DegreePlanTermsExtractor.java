package edu.wgu.android.studentscheduler.persistence;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.domain.term.TermStatus;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class DegreePlanTermsExtractor {

    private static int TERM_ID_COLUMN;
    private static int TERM_NAME_COLUMN;
    private static int TERM_START_DATE_COLUMN;
    private static int TERM_END_DATE_COLUMN;
    private static int TERM_STATUS_COLUMN;

    public static List<Term> extract(Cursor cursor) {
        init(cursor);
        List<Term> terms = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                long termId = cursor.getLong(TERM_ID_COLUMN);
                String termName = cursor.getString(TERM_NAME_COLUMN);
                String termStartDate = DateTimeUtil.getDateString(cursor.getLong(TERM_START_DATE_COLUMN));
                String termEndDate = DateTimeUtil.getDateString(cursor.getLong(TERM_END_DATE_COLUMN));
                TermStatus termStatus = TermStatus.fromStatus((cursor.getString(TERM_STATUS_COLUMN)));

                terms.add(new Term(termId, termName, termStartDate, termEndDate, new ArrayList<>(), termStatus));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return terms;
    }

    public static void init(Cursor cursor) {
        TERM_ID_COLUMN = cursor.getColumnIndex("term_id");
        TERM_NAME_COLUMN = cursor.getColumnIndex("term_name");
        TERM_START_DATE_COLUMN = cursor.getColumnIndex("term_start_date");
        TERM_END_DATE_COLUMN = cursor.getColumnIndex("term_end_date");
        TERM_STATUS_COLUMN = cursor.getColumnIndex("term_status");
    }

}
