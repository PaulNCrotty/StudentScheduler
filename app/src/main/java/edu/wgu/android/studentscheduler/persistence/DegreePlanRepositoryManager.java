package edu.wgu.android.studentscheduler.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.persistence.contract.DegreePlanContract;

import static edu.wgu.android.studentscheduler.util.StringUtil.isEmpty;

public class DegreePlanRepositoryManager extends SQLiteOpenHelper {

    private static DegreePlanRepositoryManager repositoryManager;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StudentScheduler.db";

    public static DegreePlanRepositoryManager getInstance(Context context) {
        if(repositoryManager == null) {
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
        return new MockDegreePlanRepository().getDegreePlanData(); //TODO iron out big SQL stuff here...
    }

    public long insertDegreePlan(String planName, String studentName) {
        Log.d("SQLITE_INSERT", "Inserting Plan " + planName + " into Database");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(DegreePlanContract.DegreePlan.NAME, planName);
        if(!isEmpty(studentName)) {
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
        if(!isEmpty(status)) {
            data.put(DegreePlanContract.Term.STATUS, status);
        }
        return db.insert(DegreePlanContract.Term.TABLE_NAME, null, data);
    }
}
