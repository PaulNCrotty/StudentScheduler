package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;

import static android.view.View.generateViewId;

public class StudentAssessmentListActivity extends StudentSchedulerActivity {

    public StudentAssessmentListActivity() {
        super(R.layout.activity_student_assessment_list);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String studentName = extras.getString(DEGREE_PLAN_STUDENT_NAME_BUNDLE_KEY);
        Serializable aAssessments = extras.getSerializable(ASSESSMENT_ARRAY_BUNDLE_KEY);
        ((TextView)findViewById(R.id.assessmentListHeader)).setText(getString(R.string.assessment_list_title, studentName));
        if(aAssessments instanceof ArrayList) {
            insertAssessments((ArrayList<Assessment>)aAssessments);
        }
    }


    private void insertAssessments(List<Assessment> assessments) {
        ConstraintLayout layout = findViewById(R.id.assessmentContainer);
        init();

        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int bannerConnectorId = layout.getId();
        boolean useStandardStyles = true;

        if(assessments.size() == 0) { // put place holder to help user known why the screen is empty
            TextView banner = new TextView(context, null, 0, R.style.listOptionBanner);
            TextView placeholderName = new TextView(context, null, 0, R.style.listOptionDetails);

            //set content
            banner.setId(generateViewId());
            layout.addView(banner);

            placeholderName.setId(generateViewId());
            placeholderName.setText(getString(R.string.no_assessments_found));
            layout.addView(placeholderName);

            // add constraints
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), bannerConnectorId);
            addPlanNamesConstraints(constraintSet, placeholderName.getId(), banner.getId());
            constraintSet.setMargin(placeholderName.getId(), ConstraintSet.START, marginStart);

            //prep for next iteration
            bannerConnectorId = banner.getId();
            useStandardStyles = !useStandardStyles;
        }


        for (Assessment a : assessments) {
            //declare views and prep for basic color styles
            TextView banner;
            TextView assessmentName;
            TextView assessmentDate;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetails);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }
            //set content
            banner.setId(generateViewId());
            layout.addView(banner);

            assessmentName.setId(generateViewId());
            assessmentName.setText(a.getName());
            layout.addView(assessmentName);

            assessmentDate.setId(generateViewId());
            assessmentDate.setText(a.getAssessmentDate());
            layout.addView(assessmentDate);

            // add constraints
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), bannerConnectorId);
            addPlanNamesConstraints(constraintSet, assessmentName.getId(), banner.getId());
            addModifiedDatesConstraints(constraintSet, assessmentDate.getId(), banner.getId());
            constraintSet.setMargin(assessmentName.getId(), ConstraintSet.START, marginStart);

            //prep for next iteration
            bannerConnectorId = banner.getId();
            useStandardStyles = !useStandardStyles;
        }

        constraintSet.applyTo(layout);
    }
}
