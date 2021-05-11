package edu.wgu.android.studentscheduler.persistence;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class DegreePlanCoursesExtractor {

    private static int COURSE_ID_COLUMN;
    private static int COURSE_NAME_COLUMN;
    private static int COURSE_CODE_COLUMN;
    private static int COURSE_START_DATE_COLUMN;
    private static int COURSE_END_DATE_COLUMN;
    private static int COURSE_STATUS_COLUMN;

    public static List<Course> extract(Cursor cursor) {
        init(cursor);
        List<Course> courses = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {

            do {
                long id = cursor.getLong(COURSE_ID_COLUMN);
                String name = cursor.getString(COURSE_NAME_COLUMN);
                String code = cursor.getString(COURSE_CODE_COLUMN);
                String startDate = DateTimeUtil.getDateString(cursor.getLong(COURSE_START_DATE_COLUMN));
                String endDate = DateTimeUtil.getDateString(cursor.getLong(COURSE_END_DATE_COLUMN));
                CourseStatus status = CourseStatus.fromStatus(cursor.getString(COURSE_STATUS_COLUMN));

                courses.add(new Course(id, name, code, startDate, endDate, status));
            } while (cursor.moveToNext());
        }
        return courses;
    }

    public static void init(Cursor cursor) {

        COURSE_ID_COLUMN = cursor.getColumnIndex("course_id");
        COURSE_NAME_COLUMN = cursor.getColumnIndex("course_name");
        COURSE_CODE_COLUMN = cursor.getColumnIndex("course_code");
        COURSE_START_DATE_COLUMN = cursor.getColumnIndex("course_start_date");
        COURSE_END_DATE_COLUMN = cursor.getColumnIndex("course_end_date");
        COURSE_STATUS_COLUMN = cursor.getColumnIndex("course_status");
    }

}
