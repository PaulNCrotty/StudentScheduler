package edu.wgu.android.studentscheduler.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.wgu.android.studentscheduler.entity.Assessment;
import edu.wgu.android.studentscheduler.entity.AssessmentAttempt;
import edu.wgu.android.studentscheduler.entity.Course;
import edu.wgu.android.studentscheduler.entity.CourseInstructor;
import edu.wgu.android.studentscheduler.entity.DegreePlan;
import edu.wgu.android.studentscheduler.entity.Term;
import edu.wgu.android.studentscheduler.persistence.dao.AssessmentAttemptDao;
import edu.wgu.android.studentscheduler.persistence.dao.AssessmentDao;
import edu.wgu.android.studentscheduler.persistence.dao.CourseDao;
import edu.wgu.android.studentscheduler.persistence.dao.CourseInstructorDao;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;
import edu.wgu.android.studentscheduler.persistence.dao.TermDao;

@Database(entities = {Assessment.class, AssessmentAttempt.class, Course.class,
        CourseInstructor.class, DegreePlan.class, Term.class}, version = 0)
public abstract class DegreePlanDatabase extends RoomDatabase {

    private static final String DATABASE = "degree_plan.db";

    private static DegreePlanDatabase repositoryManager;

    public static DegreePlanDatabase getInstance(Context context) {
        if (repositoryManager == null) {
            repositoryManager = Room.databaseBuilder(context, DegreePlanDatabase.class, DATABASE).build();
        }

        return repositoryManager;
    }

    public abstract AssessmentDao assessmentDao();
    public abstract AssessmentAttemptDao assessmentAttemptDao();
    public abstract CourseDao courseDao();

    public abstract CourseInstructorDao courseInstructorDao();
    public abstract DegreePlanDao degreePlanDao();
    public abstract TermDao termDao();

}
