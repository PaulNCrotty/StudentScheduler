package edu.wgu.android.studentscheduler.activity.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import edu.wgu.android.studentscheduler.activity.AssessmentDetailsActivity;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;

import static edu.wgu.android.studentscheduler.activity.StudentSchedulerActivity.ASSESSMENT_OBJECT_BUNDLE_KEY;
import static edu.wgu.android.studentscheduler.activity.StudentSchedulerActivity.COURSE_OBJECT_BUNDLE_KEY;


/***
 * https://proandroiddev.com/is-onactivityresult-deprecated-in-activity-results-api-lets-deep-dive-into-it-302d5cf6edd
 */
public class CreateAssessmentContract extends ActivityResultContract<Course, Assessment> {


    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Course course) {
        Intent intent = new Intent(context, AssessmentDetailsActivity.class);
        Bundle extras = intent.getExtras();
        extras.putSerializable(COURSE_OBJECT_BUNDLE_KEY, course);
        return intent;
    }

    @Override
    public Assessment parseResult(int resultCode, @Nullable Intent intent) {
        Assessment assessment = null;
        Bundle extras = intent.getExtras();
        if(resultCode == Activity.RESULT_OK) {
             assessment = (Assessment) extras.getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
        }
        return assessment;
    }
}
