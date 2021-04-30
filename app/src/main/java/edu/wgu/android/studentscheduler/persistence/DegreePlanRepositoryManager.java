package edu.wgu.android.studentscheduler.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL DegreePlan");
        db.execSQL(DegreePlanContract.DegreePlan.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL Term");
        db.execSQL(DegreePlanContract.Term.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL Instructor");
        db.execSQL(DegreePlanContract.Instructor.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL Course");
        db.execSQL(DegreePlanContract.Course.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL CourseNote");
        db.execSQL(DegreePlanContract.CourseNote.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "PREPPING TO RUN DDL Assessment");
        db.execSQL(DegreePlanContract.Assessment.CREATE_TABLE_DDL);
        Log.d("SQLITE_CREATION", "FINISHED DDL");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
}
