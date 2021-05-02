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
//            "order by t.id, c.id"; //do I really need to order by?

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{ Long.valueOf(degreePlanId).toString()});
        return DegreePlanExtractor.extract(cursor); //extractor will close cursor #TODO verify
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

    public long insertTerm(long degreePlanId, String termName, int startDate, int endDate, String status) {
        Log.d("SQLITE_INSERT", "Inserting Term " + termName + " into Database for Degree Plan " + degreePlanId);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.Term.DEGREE_PLAN_ID, degreePlanId);
        data.put(DegreePlanContract.Term.NAME, termName);
        data.put(DegreePlanContract.Term.START_DATE, startDate);
        data.put(DegreePlanContract.Term.END_DATE, endDate);
        if (!isEmpty(status)) {
            data.put(DegreePlanContract.Term.STATUS, status);
        }
        return db.insert(DegreePlanContract.Term.TABLE_NAME, null, data);
    }
}
