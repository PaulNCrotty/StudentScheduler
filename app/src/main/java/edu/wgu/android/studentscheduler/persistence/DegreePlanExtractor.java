package edu.wgu.android.studentscheduler.persistence;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.domain.term.TermStatus;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.assessment.AssessmentType;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class DegreePlanExtractor {

    private static int DEGREE_PLAN_ID_COLUMN;
    private static int STUDENT_NAME_COLUMN;
    private static int DEGREE_PLAN_NAME_COLUMN;
    private static int TERM_ID_COLUMN;
    private static int TERM_NAME_COLUMN;
    private static int TERM_START_DATE_COLUMN;
    private static int TERM_END_DATE_COLUMN;
    private static int TERM_STATUS_COLUMN;
    private static int COURSE_ID_COLUMN;
    private static int COURSE_NAME_COLUMN;
    private static int COURSE_CODE_COLUMN;
    private static int COURSE_START_DATE_COLUMN;
    private static int COURSE_END_DATE_COLUMN;
    private static int COURSE_STATUS_COLUMN;
    private static int ASSESSMENT_ID_COLUMN;
    private static int ASSESSMENT_NAME_COLUMN;
    private static int ASSESSMENT_CODE_COLUMN;
    private static int ASSESSMENT_TYPE_COLUMN;

    public static DegreePlan extract(Cursor cursor) {
        init(cursor);
        //initialize containers to storing and tracking extracted degree plan components
        DegreePlan degreePlan = null;
        Map<Long, Term> createdTerms = new HashMap<>();  //virtual index
        Map<Long, Course> createdCourses = new HashMap<>();

        if (cursor.moveToFirst()) {
            long degreePlanId = cursor.getLong(DEGREE_PLAN_ID_COLUMN);
            String degreePlanName = cursor.getString(DEGREE_PLAN_NAME_COLUMN);
            String studentName = cursor.getString(STUDENT_NAME_COLUMN);

            do {
                long termId = cursor.getLong(TERM_ID_COLUMN);
                Term term = createdTerms.get(termId);
                if(termId != 0 && term == null) {
                    // we haven't extracted this term yet....
                    String termName = cursor.getString(TERM_NAME_COLUMN);
                    String termStartDate = DateTimeUtil.getDateString(cursor.getLong(TERM_START_DATE_COLUMN));
                    String termEndDate = DateTimeUtil.getDateString(cursor.getLong(TERM_END_DATE_COLUMN));
                    TermStatus termStatus = TermStatus.fromStatus((cursor.getString(TERM_STATUS_COLUMN)));

                    term = new Term(termId, termName, termStartDate, termEndDate, new ArrayList<>(), termStatus);
                    createdTerms.put(termId, term);
                }

                long courseId = cursor.getLong(COURSE_ID_COLUMN);
                Course course = createdCourses.get(courseId);
                if(courseId != 0 && course == null) {
                    //we haven't extracted this course yet....
                    String courseName = cursor.getString(COURSE_NAME_COLUMN);
                    String courseCode = cursor.getString(COURSE_CODE_COLUMN);

                    //course start and end dates are not required; need to check against null values from db (which surface as 0s during extraction)
                    long cStartDate = cursor.getLong(COURSE_START_DATE_COLUMN);
                    long cEndDate = cursor.getLong(COURSE_END_DATE_COLUMN);
                    String courseStartDate = cStartDate == 0 ? null : DateTimeUtil.getDateString(cStartDate);
                    String courseEndDate = cEndDate == 0 ? null: DateTimeUtil.getDateString(cEndDate);

                    CourseStatus courseStatus = CourseStatus.fromStatus(cursor.getString(COURSE_STATUS_COLUMN));

                    //create course
                    course = new Course(courseId, courseName, courseCode, courseStartDate, courseEndDate, courseStatus, null, new ArrayList<>(), null);
                    term.getCourses().add(course);  //TODO does this update the reference used by parent container (i.e. degreePlan) properly?
                    createdCourses.put(courseId, course);
                }

                //assessments should be one-to-one (row to assessment)
                long assessmentId = cursor.getLong(ASSESSMENT_ID_COLUMN);
                if(assessmentId != 0) {
                    String assessmentName = cursor.getString(ASSESSMENT_NAME_COLUMN);
                    String assessmentCode = cursor.getString(ASSESSMENT_CODE_COLUMN);
                    AssessmentType assessmentType = AssessmentType.fromType(cursor.getString(ASSESSMENT_TYPE_COLUMN));

                    course.getAssessments().add(new Assessment(assessmentId, assessmentName, assessmentCode, null, assessmentType)); //TODO does this update the reference used by parent containers (i.e. terms and degreePlan) properly?
                }

            } while (cursor.moveToNext());

            degreePlan = new DegreePlan(degreePlanId, degreePlanName, studentName, new ArrayList<>(createdTerms.values()));
        }

        cursor.close(); //TODO can we close here?
        return degreePlan;
    }

    public static void init(Cursor cursor) {
        DEGREE_PLAN_ID_COLUMN = cursor.getColumnIndex("degree_plan_id");
        STUDENT_NAME_COLUMN = cursor.getColumnIndex("student_name");
        DEGREE_PLAN_NAME_COLUMN = cursor.getColumnIndex("degree_plan_name");
        TERM_ID_COLUMN = cursor.getColumnIndex("term_id");
        TERM_NAME_COLUMN = cursor.getColumnIndex("term_name");
        TERM_START_DATE_COLUMN = cursor.getColumnIndex("term_start_date");
        TERM_END_DATE_COLUMN = cursor.getColumnIndex("term_end_date");
        TERM_STATUS_COLUMN = cursor.getColumnIndex("term_status");
        COURSE_ID_COLUMN = cursor.getColumnIndex("course_id");
        COURSE_NAME_COLUMN = cursor.getColumnIndex("course_name");
        COURSE_CODE_COLUMN = cursor.getColumnIndex("course_code");
        COURSE_START_DATE_COLUMN = cursor.getColumnIndex("course_start_date");
        COURSE_END_DATE_COLUMN = cursor.getColumnIndex("course_end_date");
        COURSE_STATUS_COLUMN = cursor.getColumnIndex("course_status");
        ASSESSMENT_ID_COLUMN = cursor.getColumnIndex("assessment_id");
        ASSESSMENT_NAME_COLUMN = cursor.getColumnIndex("assessment_name");
        ASSESSMENT_CODE_COLUMN = cursor.getColumnIndex("assessment_code");
        ASSESSMENT_TYPE_COLUMN = cursor.getColumnIndex("assessment_type");
    }
}
