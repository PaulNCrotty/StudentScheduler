package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.term.TermStatus;

public class TermCreationActivity extends StudentSchedulerActivity {

    private long degreePlanId;

    public TermCreationActivity() {
        super(R.layout.activity_new_term_creation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        Bundle extras = getIntent().getExtras();
        degreePlanId = extras.getLong(DEGREE_PLAN_ID_BUNDLE_KEY);
    }

    TermStatus getSelectedStatus() {
        TermStatus status = TermStatus.FUTURE_UNAPPROVED;
        if (((RadioButton) findViewById(R.id.plannedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_UNAPPROVED;
        } else if (((RadioButton) findViewById(R.id.approvedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_APPROVED;
        } else if (((RadioButton) findViewById(R.id.enrolledStatusButton)).isChecked()) {
            status = TermStatus.CURRENT;
        }
        return status;
    }

    public void verifyAndSubmitTerm(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        Set<Integer> validValues = new HashSet<>();

        String name = getRequiredTextValue(R.id.termNameEditText, invalidValues, validValues);
        long startDate = getRequiredDate(R.id.termStartDateEditText, invalidValues);
        long endDate = getRequiredDate(R.id.termEndDateEditText, invalidValues);

        TermStatus selectedStatus = getSelectedStatus();

        if (startDate > endDate) {
            invalidValues.add(R.id.termStartDateEditText);
            invalidValues.add(R.id.termEndDateEditText);
        }

        if (invalidValues.size() > 0) {
            for (Integer i : invalidValues) {
                findViewById(i).setBackground(errorBorder);
            }
            Toast.makeText(this, "Fix your input before saving", Toast.LENGTH_SHORT).show();
        } else {
            repositoryManager.insertTerm(degreePlanId, name, startDate, endDate, selectedStatus.getStatus());
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }


}
