package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.assessment.AssessmentType;

public class AssessmentDetailsActivity extends StudentSchedulerActivity {

    private long courseStartDate;
    private long courseEndDate;

    public AssessmentDetailsActivity() {
        super(R.layout.activity_assessment_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            courseStartDate = extras.getLong(COURSE_START_DATE_BUNDLE_KEY);
            courseEndDate = extras.getLong(COURSE_END_DATE_BUNDLE_KEY);
            Assessment assessment = (Assessment) extras.getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
            if(assessment != null) {
                ((EditText) findViewById(R.id.assessmentNameEditText)).setText(assessment.getName());
                ((EditText) findViewById(R.id.assessmentCodeEditText)).setText(assessment.getCode());
                ((EditText) findViewById(R.id.assessmentDateEditText)).setText(assessment.getAssessmentDate());

                setTypeButton(assessment);
            }
        }
    }

    private void setTypeButton(Assessment assessment) {
        AssessmentType type = assessment.getType();
        switch (type) {
            case PERFORMANCE:
                ((RadioButton) findViewById(R.id.performanceAssessmentButton)).setChecked(true);
                break;
            case OBJECTIVE:
                ((RadioButton) findViewById(R.id.objectiveAssessmentButton)).setChecked(true);
                break;
            default:
                Toast.makeText(this, type.getType() + " is not yet supported. How did we get here?", Toast.LENGTH_SHORT).show();
        }
    }


    private AssessmentType getType() {
        AssessmentType type = null;
        if(((RadioButton) findViewById(R.id.performanceAssessmentButton)).isChecked()) {
            type = AssessmentType.PERFORMANCE;
        }

        else if (((RadioButton) findViewById(R.id.objectiveAssessmentButton)).isChecked()) {
            type  = AssessmentType.OBJECTIVE;
        }
        return type;
    }

    public void verifyAndAddAssessmentToCourse(View view) {
        Set<Integer> invalidValues = new HashSet<>();

        String name = getRequiredTextValue(R.id.assessmentNameEditText, invalidValues);
        String code = getRequiredTextValue(R.id.assessmentCodeEditText, invalidValues);
        String date = getRequiredTextValue(R.id.assessmentDateEditText, invalidValues);
        AssessmentType type = getType();
        if(type == null) {
            invalidValues.add(R.id.assessmentTypeSelectionGroup);
        }

        Assessment assessment = new Assessment(null, name, code, date, type);
        Intent intent = getIntent();
        intent.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
        setResult(RESULT_OK, intent);
        finish();
    }

}
