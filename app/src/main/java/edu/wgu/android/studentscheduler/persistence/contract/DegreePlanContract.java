package edu.wgu.android.studentscheduler.persistence.contract;

public class DegreePlanContract {

    private static final String ID = "ID";

    private DegreePlanContract() {}

    public static class DegreePlan extends DegreePlanContract {
        public static final String NAME = "NAME";
    }

    public static class Term extends DegreePlanContract {
        public static final String DEGREE_PLAN_ID = "PLAN_ID";
        public static final String NAME = "NAME";
        public static final String START_DATE = "START_DATE";
        public static final String END_DATE = "END_DATE";
    }

    public static class Course extends DegreePlanContract {
        public static final String NAME = "NAME";
        public static final String CODE = "CODE";
    }

    public static class ScheduledCourse extends DegreePlanContract {
        public static final String TERM_ID = "TERM_ID";
        public static final String COURSE_ID = "COURSE_ID";
        public static final String START_DATE = "START_DATE";
        public static final String END_DATE = "END_DATE";
        public static final String INSTRUCTOR = "INSTRUCTOR_ID";
    }

    public static class ScheduledCourseNote extends DegreePlanContract {
        public static final String SCHEDULED_COURSE_ID = "SCHEDULED_COURSE_ID";
        public static final String NOTE = "NOTE";
        public static final String CREATED_DATE = "CREATED_DATE";
    }

    public static class CourseInstructor extends DegreePlanContract {
        public static final String FIRST_NAME = "FIRST_NAME";
        public static final String LAST_NAME = "LAST_NAME";
        public static final String PHONE_NUMBER = "PHONE_NUMBER";
        public static final String EMAIL = "EMAIL";
    }

    public static class Assessment extends DegreePlanContract {
        public static final String NAME = "NAME";
        public static final String CODE = "CODE";
        public static final String CUTOFF_SCORE = "CUTOFF_SCORE";  //defaults to 100 for performance types?
        public static final String TYPE_ID = "TYPE_ID";
    }

    public static class AssessmentTask extends DegreePlanContract {
        public static final String NAME = "NAME";
        public static final String DESCRIPTION = "DESCRIPTION";
        public static final String ASSESSMENT_ID = "ASSESSMENT_ID";
    }

    // link table to support many-to-many relationship between courses and assessments
    public static class CourseAndAssessment extends DegreePlanContract {
        public static final String COURSE_ID = "COURSE_ID";
        public static final String ASSESSMENT_ID = "ASSESSMENT_ID";
    }

    public static class AssessmentAttempt extends DegreePlanContract {
        public static final String SCORE = "SCORE";
        public static final String ATTEMPT_DATE = "ATTEMPT_DATE";
        public static final String ASSESSMENT_ID = "ASSESSMENT_ID";
    }

}
