package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;
import edu.wgu.android.studentscheduler.fragment.MissingRequiredValueDialogFragment;
import edu.wgu.android.studentscheduler.persistence.DegreePlanRepositoryManager;

import static edu.wgu.android.studentscheduler.util.StringUtil.isEmpty;

public class DegreePlanCreationActivity extends StudentSchedulerActivity {

    private DegreePlanRepositoryManager repositoryManager = DegreePlanRepositoryManager.getInstance(this);

    public DegreePlanCreationActivity() {
        super(R.layout.activity_new_degreeplan_creation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    public void verifyAndSubmitPlan(View view) {
        String planName = getEditTextValue(R.id.planNameEditText);
        String studentName = getEditTextValue(R.id.studentNameEditText);
        String termName = getEditTextValue(R.id.termNameEditText);
        String termStartDate = getEditTextValue(R.id.termStartDateEditText);
        String termEndDate = getEditTextValue(R.id.termEndDateEditText);
        String termStatus = getRadioGroupSelection(R.id.progressStatusSelectionGroup);

        Set<Integer> invalidValues = new HashSet<>();
        if (isEmpty(planName)) {
            invalidValues.add(R.id.planNameEditText);
        }
        if (isEmpty(termName)) {
            invalidValues.add(R.id.termNameEditText);
        }

        // must set to null explicitly for null check prior to date comparison below
        int termStartDateSeconds = 0;
        if (isEmpty(termStartDate)) {
            invalidValues.add(R.id.termStartDateEditText);
        } else {
            termStartDateSeconds = getSecondsSinceEpoch(termStartDate);
            if(termStartDateSeconds == 0) {
                invalidValues.add(R.id.termStartDateEditText);
            }
        }

        // must set to null explicitly for null check prior to date comparison below
        int termEndDateSeconds =  0;
        if (isEmpty(termEndDate)) {
            invalidValues.add(R.id.termEndDateEditText);
        } else {
            termEndDateSeconds = getSecondsSinceEpoch(termEndDate);
            if(termEndDateSeconds == 0) {
                invalidValues.add(R.id.termEndDateEditText);
            }
        }

        if(termStartDateSeconds != 0 && termEndDateSeconds != 0) {
            if(termStartDateSeconds > termEndDateSeconds) {
                invalidValues.add(R.id.termStartDateEditText);
                invalidValues.add(R.id.termEndDateEditText);
                String title = "INVALID TERM DATES";
                String message = "The term start date must be before the term end date";
                GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment(title, message);
                errorDialog.show(getSupportFragmentManager(), "dateErrors");
            }
        }

        if (invalidValues.size() > 0) {
            for (Integer id : invalidValues) {
                findViewById(id).setBackgroundColor(invalidEntryColor);
            }

            MissingRequiredValueDialogFragment missingDialog = new MissingRequiredValueDialogFragment();
            missingDialog.show(getSupportFragmentManager(), "missingRequiredValues");
        } else {
            long degreePlanId = repositoryManager.insertDegreePlan(planName, studentName);
            long termId = repositoryManager.insertTerm(degreePlanId, termName, termStartDateSeconds, termEndDateSeconds, termStatus.toUpperCase());
        }

    }

}
