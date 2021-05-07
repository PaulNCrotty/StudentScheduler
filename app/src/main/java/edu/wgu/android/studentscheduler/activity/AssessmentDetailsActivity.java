package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.assessment.AssessmentType;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class AssessmentDetailsActivity extends StudentSchedulerActivity {

    private static final String ORIGINAL_ASSESSMENT_KEY = "edu.wgu.android.studentscheduler.activity.originalAssessment";

    private boolean isNewItem;
    private int arrayIndexKey;
    private long courseStartDate;
    private long courseEndDate;
    private Assessment originalAssessment;


    public AssessmentDetailsActivity() {
        super(R.layout.activity_assessment_detail);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (originalAssessment != null) {
            savedInstanceState.putSerializable(ORIGINAL_ASSESSMENT_KEY, originalAssessment);
            savedInstanceState.putLong(COURSE_START_DATE_BUNDLE_KEY, courseStartDate);
            savedInstanceState.putLong(COURSE_END_DATE_BUNDLE_KEY, courseEndDate);
            savedInstanceState.putInt(ARRAY_INDEX_KEY, arrayIndexKey);
            savedInstanceState.putBoolean(IS_NEW_ITEM, isNewItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //originalAssessment will only exist from savedInstanceStance if
        // 1) an assessment existed during the first load (was loaded for modification; therefore, the intent contained extras), and
        // 2) this load is not the first load
        if(savedInstanceState != null) {
            Log.d("ASSESSMENT_LOADING","**loading a pre-saved assessment**");
            originalAssessment = (Assessment) savedInstanceState.getSerializable(ORIGINAL_ASSESSMENT_KEY);
            courseStartDate = savedInstanceState.getLong(COURSE_START_DATE_BUNDLE_KEY);
            courseEndDate = savedInstanceState.getLong(COURSE_END_DATE_BUNDLE_KEY);
            arrayIndexKey = savedInstanceState.getInt(ARRAY_INDEX_KEY);
            isNewItem = savedInstanceState.getBoolean(IS_NEW_ITEM);
        } else {
            Log.d("ASSESSMENT_LOADING", "*** is first load ***");
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                courseStartDate = extras.getLong(COURSE_START_DATE_BUNDLE_KEY);
                courseEndDate = extras.getLong(COURSE_END_DATE_BUNDLE_KEY);
                arrayIndexKey = extras.getInt(ARRAY_INDEX_KEY);
                isNewItem = extras.getBoolean(IS_NEW_ITEM);
                Assessment assessment = (Assessment) extras.getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
                if (assessment != null) {
                    originalAssessment = assessment;
                    ((EditText) findViewById(R.id.assessmentNameEditText)).setText(assessment.getName());
                    ((EditText) findViewById(R.id.assessmentCodeEditText)).setText(assessment.getCode());
                    ((EditText) findViewById(R.id.assessmentDateEditText)).setText(assessment.getAssessmentDate());

                    setTypeButton(assessment);
                }
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

        long dateInSeconds = DateTimeUtil.getSecondsSinceEpoch(date);
        String message = null;
        if(dateInSeconds == 0) {
            invalidValues.add(R.id.assessmentDateEditText);
            message = "Please provide an assessment date.";
        } else if(dateInSeconds < courseStartDate || dateInSeconds > courseEndDate) {
            invalidValues.add(R.id.assessmentDateEditText);
            message = "Assessment Date must be within course start and end dates: " +
                    DateTimeUtil.getDateString(courseStartDate) + " - " + DateTimeUtil.getDateString(courseEndDate) + ".";
        }

        AssessmentType type = getType();
        if(type == null) {
            invalidValues.add(R.id.assessmentTypeSelectionGroup);
        }

        if(invalidValues.size() > 0) {
            for(Integer id: invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }
            String title = "INVALID OR  MISSING FIELDS";
            String generalMessage = "Please update the invalid or missing fields then re-submit.";
            message = message == null ? generalMessage : generalMessage + "\n Also note that " + message;
            GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment(title, message);
            errorDialog.show(getSupportFragmentManager(), "missingAssessmentFields");
        } else {
            Assessment assessment = new Assessment(null, name, code, date, type);
            Intent intent = getIntent();
            if(originalAssessment != null) {
                assessment.setId(originalAssessment.getId());
                intent.putExtra(IS_MODIFIED, !assessment.equals(originalAssessment));
            }
            intent.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
