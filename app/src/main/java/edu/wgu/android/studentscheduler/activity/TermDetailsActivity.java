package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.InvalidObjectException;
import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.domain.term.TermStatus;

public class TermDetailsActivity extends StudentSchedulerActivity {

    private long degreePlanId;
    private Term term;

    public TermDetailsActivity() {
        super(R.layout.activity_term_detail);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);
        if (term != null) {
            savedInstanceState.putSerializable(TERM_OBJECT_BUNDLE_KEY, term);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            degreePlanId = savedInstanceState.getLong(DEGREE_PLAN_ID_BUNDLE_KEY);
            term = (Term) savedInstanceState.getSerializable(TERM_OBJECT_BUNDLE_KEY);
        }

        Bundle extras = getIntent().getExtras();
        degreePlanId = (long) extras.get(DEGREE_PLAN_ID_BUNDLE_KEY);

        Term term = (Term) extras.get(TERM_OBJECT_BUNDLE_KEY);
        if (term != null) {
            ((EditText) findViewById(R.id.termNameEditText)).setText(term.getTermName());
            ((EditText) findViewById(R.id.termStartDateEditText)).setText(term.getStartDate());
            ((EditText) findViewById(R.id.termEndDateEditText)).setText(term.getEndDate());
            setSelectedStatus(term.getStatus());
        }
    }

    TermStatus getSelectedStatus() {
        TermStatus status = TermStatus.FUTURE_UNAPPROVED;
        if (((RadioButton) findViewById(R.id.plannedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_UNAPPROVED;
        } else if (((RadioButton) findViewById(R.id.approvedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_APPROVED;
        } else if (((RadioButton) findViewById(R.id.enrolledStatusButton)).isChecked()) {
            status = TermStatus.CURRENT;
        } else if (((RadioButton) findViewById(R.id.incompleteStatusButton)).isChecked()) {
            status = TermStatus.PAST_INCOMPLETE;
        } else if (((RadioButton) findViewById(R.id.completedStatusButton)).isChecked()) {
            status = TermStatus.PAST_COMPLETE;
        }
        return status;
    }

    void setSelectedStatus(TermStatus status) {
        switch (status) {
            case FUTURE_UNAPPROVED:
                ((RadioButton) findViewById(R.id.plannedStatusButton)).setChecked(true);
                break;
            case FUTURE_APPROVED:
                ((RadioButton) findViewById(R.id.approvedStatusButton)).setChecked(true);
                break;
            case CURRENT:
                ((RadioButton) findViewById(R.id.enrolledStatusButton)).setChecked(true);
                break;
            case PAST_INCOMPLETE:
                ((RadioButton) findViewById(R.id.incompleteStatusButton)).setChecked(true);
                break;
            case PAST_COMPLETE:
                ((RadioButton) findViewById(R.id.completedStatusButton)).setChecked(true);
                break;
        }
    }

    public void saveTerm(View view) {
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
            if (term == null) {
                repositoryManager.insertTerm(degreePlanId, name, startDate, endDate, selectedStatus.getStatus());
            } else {
                repositoryManager.updateTerm(term.getId(), name, startDate, endDate, selectedStatus.getStatus());
            }
            finish();
        }

    }

}
