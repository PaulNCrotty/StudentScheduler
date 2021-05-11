package edu.wgu.android.studentscheduler.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.persistence.contract.DegreePlanContract;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static edu.wgu.android.studentscheduler.util.StringUtil.isEmpty;

public class DegreePlanRepositoryManager extends SQLiteOpenHelper {

    private static DegreePlanRepositoryManager repositoryManager;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StudentScheduler.db";

    public static DegreePlanRepositoryManager getInstance(Context context) {
        if (repositoryManager == null) {
            repositoryManager = new DegreePlanRepositoryManager(context);
        }
        return repositoryManager;
    }

    private DegreePlanRepositoryManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DegreePlanContract.DegreePlan.CREATE_TABLE_DDL);
        db.execSQL(DegreePlanContract.Term.CREATE_TABLE_DDL);
        db.execSQL(DegreePlanContract.Instructor.CREATE_TABLE_DDL);
        db.execSQL(DegreePlanContract.Course.CREATE_TABLE_DDL);
        db.execSQL(DegreePlanContract.CourseNote.CREATE_TABLE_DDL);
        db.execSQL(DegreePlanContract.Assessment.CREATE_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DegreePlan getDegreePlanData(long degreePlanId) {
        String query =
            "select " +
                "dp.id as degree_plan_id, " +
                "dp.student_name as student_name, " +
                "dp.name as degree_plan_name, " +
                "t.id as term_id, " +
                "t.name as term_name, " +
                "t.start_date as term_start_date, " +
                "t.end_date as term_end_date, " +
                "t.status as term_status, "  +
                "c.id as course_id, " +
                "c.name as course_name, " +
                "c.code as course_code, " +
                "c.start_date as course_start_date, " +
                "c.end_date as course_end_date, " +
                "c.status as course_status, " +
                "a.id as assessment_id, " +
                "a.name as assessment_name, " +
                "a.code as assessment_code, " +
                "a.type as assessment_type " +
            "from degree_plan dp " +
            "left join term t on t.plan_id = dp.id " +
            "left join course c on c.term_id = t.id " +
            "left join assessment a on a.course_id = c.id " +
            "where dp.id = ? ";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{ Long.valueOf(degreePlanId).toString()});
        return DegreePlanExtractor.extract(cursor); //extractor will close cursor #TODO verify
    }

    public List<Term> getPlanTerms(long degreePlanId) {
        String query =
            "select " +
                "t.id as term_id, " +
                "t.name as term_name, " +
                "t.start_date as term_start_date, " +
                "t.end_date as term_end_date, " +
                "t.status as term_status " +
            "from degree_plan dp " +
            "left join term t on t.plan_id = dp.id " +
            "where dp.id = ? " +
            "order by t.start_date";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{Long.valueOf(degreePlanId).toString()});
        return DegreePlanTermsExtractor.extract(cursor);
    }

    public List<Course> getTermCourses(List<Long> termIds) {
        String idStringList = getIdStringList(termIds);
        String query =
            "select " +
                "t.id as term_id, " +
                "c.id as course_id, " +
                "c.name as course_name, " +
                "c.code as course_code, " +
                "c.start_date as course_start_date, " +
                "c.end_date as course_end_date, " +
                "c.status as course_status " +
                "from term t " +
                "join course c on c.term_id = t.id " +
                "where t.id IN (?)" +
                "order by c.start_date";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{idStringList});
        return DegreePlanCoursesExtractor.extract(cursor);
    }

    private String getIdStringList(List<Long> ids) {
        String idStringList = null;
        if(ids.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(ids.get(0));
            for (int i = 1; i < ids.size(); i++) {
                    sb.append(", ").append(ids.get(i));
            }
            idStringList = sb.toString();
        }

        return idStringList;
    }

    public List<Course> getTermCourses(long termId) {
        String query =
                "select " +
                    "c.id as course_id, " +
                    "c.name as course_name, " +
                    "c.code as course_code, " +
                    "c.start_date as course_start_date, " +
                    "c.end_date as course_end_date, " +
                    "c.status as course_status " +
                "from term t " +
                "join course c on c.term_id = t.id " +
                "where t.id = ? " +
                "order by c.start_date";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{Long.valueOf(termId).toString()});
        return DegreePlanCoursesExtractor.extract(cursor);
    }

    public Course getCourseDetails(long courseId) {
        String query =
                "select " +
                    "c.id as course_id, " +
                    "c.name as course_name, " +
                    "c.code as course_code, " +
                    "c.start_date as course_start_date, " +
                    "c.end_date as course_end_date, " +
                    "c.status as course_status, " +
                    "i.id as instructor_id, " +
                    "i.first_name as instructor_first, " +
                    "i.last_name as instructor_last, " +
                    "i.phone as instructor_phone, " +
                    "i.email as instructor_email, " +
                    "a.id as assessment_id, " +
                    "a.name as assessment_name, " +
                    "a.code as assessment_code, " +
                    "a.date as assessment_date, " +
                    "a.type as assessment_type " +
                "from course c " +
                    "left join instructor i on i.id = c.instructor_id " +
                    "left join assessment a on a.course_id = c.id " +
                "where c.id = ? ";


        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{ Long.valueOf(courseId).toString()});
        return CourseDetailsExtractor.extract(cursor); //extractor will close cursor #TODO verify
    }

    public List<String> getCourseNotes(long courseId) {
        String query =
                "select " +
                    "id as course_note_id, " +
                    "note as course_note " +
                "from course_note where course_id = ? ";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{ Long.valueOf(courseId).toString()});

        List<String> notes = new ArrayList<>(cursor.getCount());
        if(cursor.moveToFirst()) {
            int courseNoteIdColumn = cursor.getColumnIndex("course_note_id");
            int courseNoteColumn = cursor.getColumnIndex("course_note");
            do {
                long noteId = cursor.getLong(courseNoteIdColumn);
                if (noteId > 0) {
                    notes.add(cursor.getString(courseNoteColumn));
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
        return notes;
    }

    public List<DegreePlanDao> getBasicDegreePlanDataForAllPlans() {
        String query = "SELECT * FROM " + DegreePlanContract.DegreePlan.TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        System.out.println("Number of rows retrieved: " + count);
        List<DegreePlanDao> degreePlans = new ArrayList<>(count);
        if (cursor.moveToFirst()) {
            int pkIndex = cursor.getColumnIndex(DegreePlanContract.getId());
            int planNameIndex = cursor.getColumnIndex(DegreePlanContract.DegreePlan.NAME);
            int studentNameIndex = cursor.getColumnIndex(DegreePlanContract.DegreePlan.STUDENT_NAME);
            int createdDateIndex = cursor.getColumnIndex(DegreePlanContract.DegreePlan.CREATED_DATE);
            int modifiedDateIndex = cursor.getColumnIndex(DegreePlanContract.DegreePlan.MODIFIED_DATE);

            do {

                long id = cursor.getLong(pkIndex);
                String planName = cursor.getString(planNameIndex);
                String studentName = cursor.getString(studentNameIndex);
                long createdDate = cursor.getLong(createdDateIndex);
                long modifiedDate = cursor.getLong(modifiedDateIndex);
                DegreePlanDao e = new DegreePlanDao(id, planName, studentName, createdDate, modifiedDate);
                degreePlans.add(e);
                Log.d("DAO", e.toString());

            } while (cursor.moveToNext());
        }

        System.out.println("Number of rows parsed: " + degreePlans.size());

        cursor.close();
        Collections.sort(degreePlans, new DegreePlanDao.AuditDateComparator());
        return degreePlans;
    }

    public long insertDegreePlan(String planName, String studentName) {
        Log.d("SQLITE_INSERT", "Inserting Plan " + planName + " into Database");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.DegreePlan.NAME, planName);
        data.put(DegreePlanContract.DegreePlan.CREATED_DATE, DateTimeUtil.getSecondsSinceEpoch());
        if (!isEmpty(studentName)) {
            data.put(DegreePlanContract.DegreePlan.STUDENT_NAME, studentName);
        }
        return db.insert(DegreePlanContract.DegreePlan.TABLE_NAME, null, data);
    }

    public long insertTerm(long degreePlanId, String termName, long startDate, long endDate, String status) {
        Log.d("SQLITE_INSERT", "Inserting Term " + termName + " into Database for Degree Plan " + degreePlanId);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Term.DEGREE_PLAN_ID, degreePlanId);
        data.put(DegreePlanContract.Term.NAME, termName);
        data.put(DegreePlanContract.Term.START_DATE, startDate);
        data.put(DegreePlanContract.Term.END_DATE, endDate);
        if (!isEmpty(status)) {
            data.put(DegreePlanContract.Term.STATUS, status.toUpperCase());
        }
        return db.insert(DegreePlanContract.Term.TABLE_NAME, null, data);
    }

    public int updateTerm(long termId, String termName, long startDate, long endDate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Term.NAME, termName);
        data.put(DegreePlanContract.Term.START_DATE, startDate);
        data.put(DegreePlanContract.Term.END_DATE, endDate);
        if (!isEmpty(status)) {
            data.put(DegreePlanContract.Term.STATUS, status);
        }
        return db.update(DegreePlanContract.Term.TABLE_NAME, data, "ID = ?", new String[]{Long.valueOf(termId).toString()});
    }

    public long insertInstructor(String firstName, String lastName, String phoneArea, String phonePrefix, String phoneSuffix, String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Instructor.FIRST_NAME, firstName);
        data.put(DegreePlanContract.Instructor.LAST_NAME, lastName);
        data.put(DegreePlanContract.Instructor.PHONE_NUMBER, phoneArea + "-" + phonePrefix + "-" + phoneSuffix);
        data.put(DegreePlanContract.Instructor.EMAIL, email);

        return db.insert(DegreePlanContract.Instructor.TABLE_NAME, null, data);
    }

    public int updateCourseInstructor(CourseInstructor instructor) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Instructor.FIRST_NAME, instructor.getFirstName());
        data.put(DegreePlanContract.Instructor.LAST_NAME, instructor.getLastName());
        data.put(DegreePlanContract.Instructor.PHONE_NUMBER, instructor.getPhoneNumber());
        data.put(DegreePlanContract.Instructor.EMAIL, instructor.getEmail());

        return db.update(DegreePlanContract.Instructor.TABLE_NAME, data, "ID = ?", new String[]{instructor.getId().toString()});
    }

    public long insertCourse(long termId, long instructorId, String courseName, String courseCode, long courseStartDate, long courseEndDate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Course.TERM_ID, termId);
        data.put(DegreePlanContract.Course.INSTRUCTOR, instructorId);
        data.put(DegreePlanContract.Course.NAME, courseName);
        data.put(DegreePlanContract.Course.CODE, courseCode);
        data.put(DegreePlanContract.Course.STATUS, status.toUpperCase());

        if(courseStartDate !=  0) {
            data.put(DegreePlanContract.Course.START_DATE, courseStartDate);
        }
        if(courseEndDate != 0) {
            data.put(DegreePlanContract.Course.END_DATE, courseEndDate);
        }
        return db.insert(DegreePlanContract.Course.TABLE_NAME, null, data);
    }

    public int updateCourse(long courseId, String courseName, String courseCode, long courseStartDate, long courseEndDate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Course.NAME, courseName);
        data.put(DegreePlanContract.Course.CODE, courseCode);
        data.put(DegreePlanContract.Course.STATUS, status.toUpperCase());

        if(courseStartDate !=  0) {
            data.put(DegreePlanContract.Course.START_DATE, courseStartDate);
        }
        if(courseEndDate != 0) {
            data.put(DegreePlanContract.Course.END_DATE, courseEndDate);
        }

        return db.update(DegreePlanContract.Course.TABLE_NAME, data, "ID = ?", new String[]{Long.valueOf(courseId).toString()});
    }

    public long[] insertAssessments(long courseId, List<Assessment> assessments) {
        long[] ids = new long[assessments.size()];
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int i = 0;
            ContentValues data = new ContentValues();
            for(Assessment a: assessments) {
                data.put(DegreePlanContract.Assessment.COURSE_ID, courseId);
                data.put(DegreePlanContract.Assessment.NAME, a.getName());
                data.put(DegreePlanContract.Assessment.CODE, a.getCode());
                data.put(DegreePlanContract.Assessment.DATE, DateTimeUtil.getSecondsSinceEpoch(a.getAssessmentDate()));
                data.put(DegreePlanContract.Assessment.TYPE, a.getType().getType());
                ids[i] = db.insert(DegreePlanContract.Assessment.TABLE_NAME, null, data);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ids;
    }

    public int[] updateAssessments(List<Assessment> assessments) {
        int[] ids = new int[assessments.size()];
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int i = 0;
            ContentValues data = new ContentValues();
            for(Assessment a: assessments) {
                data.put(DegreePlanContract.Assessment.NAME, a.getName());
                data.put(DegreePlanContract.Assessment.CODE, a.getCode());
                data.put(DegreePlanContract.Assessment.DATE, DateTimeUtil.getSecondsSinceEpoch(a.getAssessmentDate()));
                data.put(DegreePlanContract.Assessment.TYPE, a.getType().getType());
                ids[i] = db.update(DegreePlanContract.Assessment.TABLE_NAME, data, "ID = ?", new String[]{a.getId().toString()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ids;
    }

    public long[] insertCourseNotes(long courseId, List<String> notes) {
        long[] ids = new long[notes.size()];
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int i = 0;
            ContentValues data = new ContentValues();
            for(String n: notes) {
                data.put(DegreePlanContract.CourseNote.COURSE_ID, courseId);
                data.put(DegreePlanContract.CourseNote.NOTE, n);
                data.put(DegreePlanContract.CourseNote.CREATED_DATE, DateTimeUtil.getSecondsSinceEpoch());
                ids[i] = db.insert(DegreePlanContract.CourseNote.TABLE_NAME, null, data);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ids;
    }

    public int[] deleteEntries(List<Long> entryIds, String tableName) {
        int[] rowsDeleted = new int[entryIds.size()];
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            for(int i = 0; i < entryIds.size(); i++) {
                rowsDeleted[i] = db.delete(tableName, "ID = ? ", new String[]{entryIds.get(i).toString()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return rowsDeleted;
    }
}
