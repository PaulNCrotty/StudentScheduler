package edu.wgu.android.studentscheduler.persistence.extractor;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.CourseNote;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class CourseNoteExtractor {

    private static int COURSE_NOTE_ID_COLUMN;
    private static int COURSE_NOTE_TITLE_COLUMN;
    private static int COURSE_NOTE_BODY_COLUMN;
    private static int COURSE_NOTE_CREATED_DATE_COLUMN;
    private static int COURSE_NOTE_MODIFIED_DATE_COLUMN;

    public static List<CourseNote> extract(Cursor cursor) {
        init(cursor);
        List<CourseNote> courseNotes = new ArrayList<>(cursor.getCount());
        if(cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(COURSE_NOTE_ID_COLUMN);
                String title = cursor.getString(COURSE_NOTE_TITLE_COLUMN);
                String body = cursor.getString(COURSE_NOTE_BODY_COLUMN);
                String createdDate = DateTimeUtil.getDateString(cursor.getLong(COURSE_NOTE_CREATED_DATE_COLUMN));

                long modifiedDateAsLong = cursor.getLong(COURSE_NOTE_MODIFIED_DATE_COLUMN);

                String modifiedDate = null;
                if(modifiedDateAsLong > 0) {
                    modifiedDate = DateTimeUtil.getDateString(modifiedDateAsLong);
                }

                courseNotes.add(new CourseNote(id, title, body, createdDate, modifiedDate));

            } while(cursor.moveToNext());
        }
        cursor.close();
        return courseNotes;
    }

    private static void init(Cursor cursor) {
        COURSE_NOTE_ID_COLUMN = cursor.getColumnIndex("note_id");
        COURSE_NOTE_TITLE_COLUMN = cursor.getColumnIndex("note_title");
        COURSE_NOTE_BODY_COLUMN = cursor.getColumnIndex("note_body");
        COURSE_NOTE_CREATED_DATE_COLUMN = cursor.getColumnIndex("note_created_date");
        COURSE_NOTE_MODIFIED_DATE_COLUMN = cursor.getColumnIndex("note_modified_date");
    }
}
