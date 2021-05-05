package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.MissingRequiredValueDialogFragment;

public class DegreePlanCreationActivity extends StudentSchedulerActivity {

    public DegreePlanCreationActivity() {
        super(R.layout.activity_new_degreeplan_creation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void verifyAndSubmitPlan(View view) {

//        String planName = getEditTextValue(R.id.planNameEditText);
//        String termName = getEditTextValue(R.id.termNameEditText);
//        String termStartDate = getEditTextValue(R.id.termStartDateEditText);
//        String termEndDate = getEditTextValue(R.id.termEndDateEditText);

        String studentName = getEditTextValue(R.id.studentNameEditText);
        String termStatus = getRadioGroupSelection(R.id.progressStatusSelectionGroup);

        Set<Integer> validValues = new HashSet<>();
        Set<Integer> invalidValues = new HashSet<>();
        String planName = getRequiredTextValue(R.id.planNameEditText, invalidValues, validValues);
        String termName = getRequiredTextValue(R.id.termNameEditText, invalidValues, validValues);
        int termStartDateSeconds = getRequiredDate(R.id.termStartDateEditText, invalidValues);
        int termEndDateSeconds = getRequiredDate(R.id.termEndDateEditText, invalidValues);
        verifyDates(termStartDateSeconds, termEndDateSeconds, R.id.termStartDateEditText, R.id.termEndDateEditText, invalidValues, validValues);

//        // must set to null explicitly for null check prior to date comparison below
//        int termStartDateSeconds = 0;
//        if (isEmpty(termStartDate)) {
//            invalidValues.add(R.id.termStartDateEditText);
//        } else {
//            termStartDateSeconds = getSecondsSinceEpoch(termStartDate);
//            if (termStartDateSeconds == 0) {
//                invalidValues.add(R.id.termStartDateEditText);
//            }
//        }
//
//        // must set to null explicitly for null check prior to date comparison below
//        int termEndDateSeconds = 0;
//        if (isEmpty(termEndDate)) {
//            invalidValues.add(R.id.termEndDateEditText);
//        } else {
//            termEndDateSeconds = getSecondsSinceEpoch(termEndDate);
//            if (termEndDateSeconds == 0) {
//                invalidValues.add(R.id.termEndDateEditText);
//            }
//        }
//
//        if (termStartDateSeconds != 0 && termEndDateSeconds != 0) {
//            if (termStartDateSeconds > termEndDateSeconds) {
//                invalidValues.add(R.id.termStartDateEditText);
//                invalidValues.add(R.id.termEndDateEditText);
//                String title = "INVALID TERM DATES";
//                String message = "The term start date must be before the term end date";
//                GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment(title, message);
//                errorDialog.show(getSupportFragmentManager(), "dateErrors");
//            } else {
//                validValues.add(R.id.termStartDateEditText);
//                validValues.add(R.id.termEndDateEditText);
//            }
//        }

        if (invalidValues.size() > 0) {
            for (Integer id : invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }

            // only reset background if there are still valid values; otherwise the form will be submitted
            // and the activity closes so the changes won't be noticeable
            for (Integer id : validValues) {
                findViewById(id).setBackground(null);
            }

            MissingRequiredValueDialogFragment missingDialog = new MissingRequiredValueDialogFragment();
            missingDialog.show(getSupportFragmentManager(), "missingRequiredValues");
        } else {
            long degreePlanId = repositoryManager.insertDegreePlan(planName, studentName);
            repositoryManager.insertTerm(degreePlanId, termName, termStartDateSeconds, termEndDateSeconds, termStatus.toUpperCase());
            Intent degreePlanEditorActivity = new Intent(getApplicationContext(), DegreePlanActivity.class);
            degreePlanEditorActivity.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);
            startActivity(degreePlanEditorActivity);
            finish();
        }

    }

}
