package edu.wgu.android.studentscheduler.persistence.contract;

public class DegreePlanContract {

    private static final String ID = "ID";
    private static final String ID_CONSTRAINTS = "integer not null primary key autoincrement";

    private DegreePlanContract() {
    }

    public static class DegreePlan extends DegreePlanContract {
        public static final String TABLE_NAME = "DEGREE_PLAN";
        public static final String NAME = "NAME";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" + ID + " " + ID_CONSTRAINTS + ", " + NAME + " text not null)";
    }

    public static class Term extends DegreePlanContract {
        public static final String TABLE_NAME = "TERM";
        public static final String DEGREE_PLAN_ID = "PLAN_ID";
        public static final String NAME = "NAME";
        public static final String START_DATE = "START_DATE";
        public static final String END_DATE = "END_DATE";
        public static final String STATUS = "STATUS";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" +
                ID + " " + ID_CONSTRAINTS + ", " +
                DEGREE_PLAN_ID + " integer not null references " + DegreePlan.TABLE_NAME + "(" + ID + ") on delete cascade, " +
                NAME + " text not null, " +
                START_DATE + " integer not null, " +
                END_DATE + " integer not null, " +
                STATUS + " text DEFAULT('PLANNED') CHECK (" + STATUS + " IN ('PLANNED', 'ENROLLED', 'COMPLETED', 'INCOMPLETE')))";
    }

    public static class Instructor extends DegreePlanContract {
        public static final String TABLE_NAME = "INSTRUCTOR";
        public static final String FIRST_NAME = "FIRST_NAME";
        public static final String LAST_NAME = "LAST_NAME";
        public static final String PHONE_NUMBER = "PHONE";
        public static final String EMAIL = "EMAIL";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" +
                ID + " " + ID_CONSTRAINTS + ", " +
                FIRST_NAME + " text not null, " +
                LAST_NAME + " text not null, " +
                PHONE_NUMBER + " text not null, " + EMAIL + " text not null)";
    }

    public static class Course extends DegreePlanContract {
        public static final String TABLE_NAME = "COURSE";
        public static final String TERM_ID = "TERM_ID";
        public static final String NAME = "NAME";
        public static final String CODE = "CODE";
        public static final String START_DATE = "START_DATE";
        public static final String END_DATE = "END_DATE";
        public static final String STATUS = "STATUS";
        public static final String INSTRUCTOR = "INSTRUCTOR_ID";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" +
                ID + " " + ID_CONSTRAINTS + ", " +
                TERM_ID + " integer not null references " + Term.TABLE_NAME + "(" + ID + ") on delete restrict, " +
                NAME + " text not null, " +
                CODE + " text not null, " +
                START_DATE + " integer, " +
                END_DATE + " integer, " +
                STATUS + " text CHECK(" + STATUS + " IN ('PLANNED', 'ENROLLED', 'DROPPED', 'PASSED', 'FAILED', 'INCOMPLETE')), " +
                INSTRUCTOR + " integer not null references " + Instructor.TABLE_NAME + "(" + ID + ") on delete set null)";
    }

    public static class CourseNote extends DegreePlanContract {
        public static final String TABLE_NAME = "COURSE_NOTE";
        public static final String COURSE_ID = "COURSE_ID";
        public static final String NOTE = "NOTE";
        public static final String CREATED_DATE = "CREATED_DATE";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" +
                ID + " " + ID_CONSTRAINTS + ", " +
                COURSE_ID + " integer not null references " + Course.TABLE_NAME + "(" + ID + ") on delete cascade, " +
                NOTE + " text not null, " +
                CREATED_DATE + " integer not null)";
    }

    public static class Assessment extends DegreePlanContract {
        public static final String TABLE_NAME = "ASSESSMENT";
        public static final String COURSE_ID = "COURSE_ID";
        public static final String NAME = "NAME";
        public static final String CODE = "CODE";
        public static final String DATE = "DATE";
        public static final String TYPE = "TYPE";

        public static final String CREATE_TABLE_DDL = "create table " + TABLE_NAME + "(" +
                ID + " " + ID_CONSTRAINTS + ", " +
                COURSE_ID + " integer not null references " + Course.TABLE_NAME + "(" + ID + ") on delete cascade, " +
                NAME + " text not null, " +
                CODE + " text not null, " +
                DATE + " integer not null, " +
                TYPE + " text not null CHECK(" + TYPE + " IN ('Objective', 'Performance')))";
    }

}
