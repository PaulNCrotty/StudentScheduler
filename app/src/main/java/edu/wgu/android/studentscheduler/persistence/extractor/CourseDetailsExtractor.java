package edu.wgu.android.studentscheduler.persistence.extractor;

import android.database.Cursor;

import java.util.ArrayList;

import edu.wgu.android.studentscheduler.domain.CourseNote;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.assessment.AssessmentType;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class CourseDetailsExtractor {

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
            ArrayList<CourseNote> courseNotes = new ArrayList<>();
            ArrayList<Assessment> assessments = new ArrayList<>();
            course = new Course(courseId, courseName, courseCode, courseStartDate, courseEndDate, courseStatus, instructor, assessments, courseNotes);

            do {
                long assessmentId = cursor.getLong(ASSESSMENT_ID_COLUMN);
                if (assessmentId > 0) {
                    assessments.add(extractAssessment(assessmentId, cursor));
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
    }
}
