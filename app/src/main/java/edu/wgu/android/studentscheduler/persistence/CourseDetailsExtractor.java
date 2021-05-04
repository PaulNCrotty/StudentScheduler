package edu.wgu.android.studentscheduler.persistence;

import android.database.Cursor;

import java.util.ArrayList;

import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.assessment.AssessmentType;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class CourseDetailsExtractor {

    /***
     * select
     * c.id as course_id,
     * c.name as course_name,
     * c.code as course_code,
     * c.start_date as course_start_date,
     * c.end_date as course_end_date,
     * c.status as course_status,
     * i.id as instructor_id,
     * i.first_name as instructor_first,
     * i.last_name as instructor_last,
     * i.phone as instructor_phone,
     * i.email as instructor_email,
     * a.id as assessment_id,
     * a.name as assessment_name,
     * a.code as assessment_code,
     * a.date as assessment_date,
     * a.type as assessment_type,
     * n.note as course_note
     * from course c
     * left join instructor i on i.id = c.instructor_id
     * left join assessment a on a.course_id = c.id
     * left join course_note n on n.course_id = c.id
     * where c.id = ?
     */

    private static int COURSE_ID_COLUMN;
    private static int COURSE_NAME_COLUMN;
    private static int COURSE_CODE_COLUMN;
    private static int COURSE_START_DATE_COLUMN;
    private static int COURSE_END_DATE_COLUMN;
    private static int COURSE_STATUS_COLUMN;
    private static int INSTRUCTOR_ID_COLUMN;
    private static int INSTRUCTOR_FIRST_COLUMN;
    private static int INSTRUCTOR_LAST_COLUMN;
    private static int INSTRUCTOR_PHONE_COLUMN;
    private static int INSTRUCTOR_EMAIL_COLUMN;
    private static int ASSESSMENT_ID_COLUMN;
    private static int ASSESSMENT_NAME_COLUMN;
    private static int ASSESSMENT_CODE_COLUMN;
    private static int ASSESSMENT_DATE_COLUMN;
    private static int ASSESSMENT_TYPE_COLUMN;
    private static int COURSE_NOTE_ID_COLUMN;
    private static int COURSE_NOTE_COLUMN;

    public static Course extract(Cursor cursor) {
        init(cursor);
        Course course = null;
        if(cursor.moveToFirst()) {
            long courseId = cursor.getLong(COURSE_ID_COLUMN);
            String courseName = cursor.getString(COURSE_NAME_COLUMN);
            String courseCode = cursor.getString(COURSE_CODE_COLUMN);

            //course start and end dates are not required; need to check against null values from db (which surface as 0s during extraction)
            long cStartDate = cursor.getLong(COURSE_START_DATE_COLUMN);
            long cEndDate = cursor.getLong(COURSE_END_DATE_COLUMN);
            String courseStartDate = cStartDate == 0 ? null : DateTimeUtil.getDateString(cStartDate);
            String courseEndDate = cEndDate == 0 ? null: DateTimeUtil.getDateString(cEndDate);

            CourseStatus courseStatus = CourseStatus.fromStatus(cursor.getString(COURSE_STATUS_COLUMN));

            long instructorId = cursor.getLong(INSTRUCTOR_ID_COLUMN);
            String firstName = cursor.getString(INSTRUCTOR_FIRST_COLUMN);
            String lastName = cursor.getString(INSTRUCTOR_LAST_COLUMN);
            String phoneNumber = cursor.getString(INSTRUCTOR_PHONE_COLUMN);
            String email = cursor.getString(INSTRUCTOR_EMAIL_COLUMN);
            CourseInstructor instructor = new CourseInstructor(instructorId, firstName, lastName, phoneNumber, email);

            //create course
            ArrayList<String> courseNotes = new ArrayList<>();
            ArrayList<Assessment> assessments = new ArrayList<>();
            course = new Course(courseId, courseName, courseCode, courseStartDate, courseEndDate, courseStatus, instructor, assessments, courseNotes);

            do {
                long assessmentId = cursor.getLong(ASSESSMENT_ID_COLUMN);
                if (assessmentId > 0) {
                    assessments.add(extractAssessment(assessmentId, cursor));
                }

                long noteId = cursor.getLong(COURSE_NOTE_ID_COLUMN);
                if (noteId > 0) {
                    courseNotes.add(cursor.getString(COURSE_NOTE_COLUMN));
                }
            } while(cursor.moveToNext());
        }

        cursor.close();
        return course;

    }

    private static Assessment extractAssessment(long assessmentId, Cursor cursor) {
        String name = cursor.getString(ASSESSMENT_NAME_COLUMN);
        String code = cursor.getString(ASSESSMENT_CODE_COLUMN);
        String date = DateTimeUtil.getDateString(cursor.getLong(ASSESSMENT_DATE_COLUMN));
        AssessmentType type = AssessmentType.fromType(cursor.getString(ASSESSMENT_TYPE_COLUMN));
        return new Assessment(assessmentId, name, code, date, type);
    }

    public static void init(Cursor cursor) {

        COURSE_ID_COLUMN = cursor.getColumnIndex("course_id");
        COURSE_NAME_COLUMN = cursor.getColumnIndex("course_name");
        COURSE_CODE_COLUMN = cursor.getColumnIndex("course_code");
        COURSE_START_DATE_COLUMN = cursor.getColumnIndex("course_start_date");
        COURSE_END_DATE_COLUMN = cursor.getColumnIndex("course_end_date");
        COURSE_STATUS_COLUMN = cursor.getColumnIndex("course_status");
        INSTRUCTOR_ID_COLUMN = cursor.getColumnIndex("instructor_id");
        INSTRUCTOR_FIRST_COLUMN = cursor.getColumnIndex("instructor_first");
        INSTRUCTOR_LAST_COLUMN = cursor.getColumnIndex("instructor_last");
        INSTRUCTOR_PHONE_COLUMN = cursor.getColumnIndex("instructor_phone");
        INSTRUCTOR_EMAIL_COLUMN = cursor.getColumnIndex("instructor_email");
        ASSESSMENT_ID_COLUMN = cursor.getColumnIndex("assessment_id");
        ASSESSMENT_NAME_COLUMN = cursor.getColumnIndex("assessment_name");
        ASSESSMENT_CODE_COLUMN = cursor.getColumnIndex("assessment_code");
        ASSESSMENT_DATE_COLUMN = cursor.getColumnIndex("assessment_date");
        ASSESSMENT_TYPE_COLUMN = cursor.getColumnIndex("assessment_type");
        COURSE_NOTE_ID_COLUMN = cursor.getColumnIndex("course_note_id");
        COURSE_NOTE_COLUMN = cursor.getColumnIndex("course_note");

    }
}
