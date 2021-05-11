package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.Serializable;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.widget.IndexedCheckBox;

import static android.view.View.generateViewId;

public class TermListActivity extends StudentSchedulerActivity {

    private static final String PLAN_TERMS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.planTerms";

    private static final int CREATE_TERM_RESULT = 0;
    private static final int MODIFY_TERM_RESULT = 1;

    private long degreePlanId;
    private List<Term> planTerms;

    public TermListActivity() {
        super(R.layout.activity_degree_plan_simplified);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);
        savedInstanceState.putSerializable(PLAN_TERMS_ARRAY_KEY, (Serializable) planTerms);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        Bundle extras = getIntent().getExtras();
        degreePlanId = extras.getLong(DEGREE_PLAN_ID_BUNDLE_KEY);
        String planName = extras.getString(DEGREE_PLAN_NAME_BUNDLE_KEY);
        String studentName = extras.getString(DEGREE_PLAN_STUDENT_NAME_BUNDLE_KEY);

        planTerms = getPlanTerms(degreePlanId); // always pull from DB (simple approach for now...)

        //set header and sub header text values
        TextView degreePlanMainHeader = findViewById(R.id.degreePlanDetailsMainHeader);
        degreePlanMainHeader.setText(getString(R.string.degree_plan_title, studentName));
        TextView degreePlanSubtitle = findViewById(R.id.degreePlanDetailsSubtitle);
        degreePlanSubtitle.setText(planName);
        insertTerms(planTerms);
    }

    private void insertTerms(List<Term> terms) {
        //dynamically set rows for terms
        ConstraintLayout layout = findViewById(R.id.termContainer);
        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int viewIndex = 0;
        int connectionId = layout.getId();
        boolean useStandardStyles = true;
        for(Term term: terms) {
            //High-level term one details
            TextView banner;
            IndexedCheckBox removeIcon = new IndexedCheckBox(context);
            TextView title;
            TextView dates;
            if(useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                removeIcon.setBackgroundColor(orangeColor);
                title = new TextView(context, null, 0, R.style.listOptionDetails);
                dates = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                title = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                dates = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }

            banner.setId(generateViewId());
            banner.setOnClickListener(new ModifyTermAction(viewIndex++));
            layout.addView(banner);

            removeIcon.setId(generateViewId());
            removeIcon.setViewIndex(viewIndex++);
            removeIcon.setChecked(false);
            layout.addView(removeIcon);

            title.setId(generateViewId());
            title.setOnClickListener(new ModifyTermAction(viewIndex++));
            title.setText(term.getName());
            layout.addView(title);

            dates.setId(generateViewId());
            dates.setOnClickListener(new ModifyTermAction(viewIndex++));
            dates.setText(getString(R.string.start_and_end_dates, term.getStartDate(), term.getEndDate()));
            layout.addView(dates);

            // add constraints
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), removeIcon.getId(), connectionId);
            addRemoveIconConstraint(constraintSet, removeIcon.getId(), banner.getId());
            addPlanNamesConstraints(constraintSet, title.getId(), banner.getId());
            addModifiedDatesConstraints(constraintSet, dates.getId(), banner.getId());

            connectionId = banner.getId();
            useStandardStyles = !useStandardStyles;

        }

        constraintSet.applyTo(layout);
    }

    private List<Term> getPlanTerms(long degreePlanId) {
        return repositoryManager.getPlanTerms(degreePlanId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {

            if(requestCode == MODIFY_TERM_RESULT) {
                data.getExtras().getSerializable(TERM_OBJECT_BUNDLE_KEY);
            }
        }
    }

    private class ModifyTermAction implements View.OnClickListener {

        private int viewIndex;

        ModifyTermAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        @Override
        public void onClick(View v) {
            int index = this.viewIndex / VIEWS_PER_ROW;
            Term term = planTerms.get(index);

            Intent intent = new Intent(getApplicationContext(), TermDetailsActivity.class);

            intent.putExtra(TERM_OBJECT_BUNDLE_KEY, term);
            intent.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);
            startActivityForResult(intent, MODIFY_TERM_RESULT);
        }
    }
}
