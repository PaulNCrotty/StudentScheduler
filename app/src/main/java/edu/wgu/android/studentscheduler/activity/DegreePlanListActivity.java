package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Collections;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.NoDegreePlansDialogFragment;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static android.view.View.generateViewId;

public class DegreePlanListActivity extends StudentSchedulerActivity implements NoDegreePlansDialogFragment.Listener {

    private static final int VIEWS_PER_ROW = 3;
    private static final int MAX_RECENT_TO_LIST = 3;

    private List<DegreePlanDao> planDAOs;

    /**
     * Basic no arg constructor which calls super -> super -> super -> super to prepare layout
     */
    public DegreePlanListActivity() {
        super(R.layout.activity_degree_plan_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        planDAOs = repositoryManager.getBasicDegreePlanDataForAllPlans();  //TODO shouldn't be calling a repo from a view layer
        checkForPlans();  //surface option to create plans if none exist yet in the database;
        init();  //required to instantiated and use standard colors

        //put them in reverse chronological order so newly worked on plans appear first (original sorting done in repo layer)
        Collections.reverse(planDAOs);
        int numberOfPlans = planDAOs.size();

        ConstraintLayout recentPlansContainer = findViewById(R.id.recentDegreePlansContainer);
        Context recentPlansContext = recentPlansContainer.getContext();
        ConstraintSet recentPlansConstraints = new ConstraintSet();
        recentPlansConstraints.clone(recentPlansContainer);

        ConstraintLayout remainingPlansContainer = findViewById(R.id.remainingDegreePlansContainer);
        Context remainingPlansContext = remainingPlansContainer.getContext();
        ConstraintSet remainingPlansConstraints = new ConstraintSet();
        remainingPlansConstraints.clone(remainingPlansContainer);

        int viewIndex = 0;
        int bannerConnectorId = recentPlansContainer.getId();
        boolean useStandardStyles = true;
        boolean isFirstRemainingDegreePlan = true;

        Context context = recentPlansContext;
        ConstraintLayout layout = recentPlansContainer;
        ConstraintSet constraintSet = recentPlansConstraints;
        for (int i = 0; i < numberOfPlans; i++) {

            DegreePlanDao d = planDAOs.get(i);

            //toggle to remaining plans after we hit max amount to surface in recent list
            if (i >= MAX_RECENT_TO_LIST && isFirstRemainingDegreePlan) {
                useStandardStyles = true;
                context = remainingPlansContext;
                layout = remainingPlansContainer;
                constraintSet = remainingPlansConstraints;
                isFirstRemainingDegreePlan = false;
            }

            //declare views and prep for basic color styles
            ImageView dpViewLink = new ImageView(context);
            TextView banner;
            TextView planNames;
            TextView modifiedDates;
            if (useStandardStyles) {
                dpViewLink.setBackgroundColor(orangeColor);
                dpViewLink.setImageResource(R.drawable.edit_calendar_icon_white);
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                planNames = new TextView(context, null, 0, R.style.listOptionDetails);
                modifiedDates = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                dpViewLink.setBackgroundColor(whiteColor);
                dpViewLink.setImageResource(R.drawable.edit_calendar_icon);
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                planNames = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                modifiedDates = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }
            //set content
            dpViewLink.setId(generateViewId());
            dpViewLink.setOnClickListener(v -> {
                Intent intent = new Intent(this, DegreePlanActivity.class);
                intent.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, d.getId());
                startActivity(intent);
            });
            layout.addView(dpViewLink);

            banner.setId(generateViewId());
            banner.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
            layout.addView(banner);

            planNames.setId(generateViewId());
            planNames.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
            planNames.setText(getString(R.string.degree_plan_basic_description, d.getStudentName(), d.getName()));
            layout.addView(planNames);

            modifiedDates.setId(generateViewId());
            modifiedDates.setText(DateTimeUtil.getDateString(d.getLastModified()));
            modifiedDates.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
            layout.addView(modifiedDates);

            // add constraints
            addIconConstraints(constraintSet, dpViewLink.getId(), banner.getId());
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), dpViewLink.getId(), bannerConnectorId);
            addPlanNamesConstraints(constraintSet, planNames.getId(), banner.getId());
            addModifiedDatesConstraints(constraintSet, modifiedDates.getId(), banner.getId());

            //certain constraint overrides
            constraintSet.setMargin(planNames.getId(), ConstraintSet.START, marginStart);
            constraintSet.setMargin(dpViewLink.getId(), ConstraintSet.START, 1);

            //prep for next iteration
            bannerConnectorId = banner.getId();
            useStandardStyles = !useStandardStyles;
        }

        recentPlansConstraints.applyTo(recentPlansContainer);
        remainingPlansConstraints.applyTo(remainingPlansContainer);

    }

    private void checkForPlans() {
        if (planDAOs != null && planDAOs.size() <= 0) {
            NoDegreePlansDialogFragment noPlansDialog = new NoDegreePlansDialogFragment();
            noPlansDialog.show(getSupportFragmentManager(), "noPlansDialog");
        }
    }

    @Override
    public void onPositive() {
        Intent degreePlanCreation = new Intent(getApplicationContext(), DegreePlanCreationActivity.class);
        startActivity(degreePlanCreation);
        finish();
    }

    public void deleteSelectedPlans(View view) {
        Toast.makeText(this, "Deletion of Degree Plans is not yet supported", Toast.LENGTH_LONG).show();
    }

    public void createNewPlan(View view) {
        startActivity(new Intent(this, DegreePlanCreationActivity.class));
    }

    private class ModifyDegreePlanAction implements View.OnClickListener {

        ModifyDegreePlanAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private int viewIndex;

        @Override
        public void onClick(View v) {
            int index = this.viewIndex / VIEWS_PER_ROW;
            DegreePlanDao targetPlan = planDAOs.get(index);
            String message = "You bonked on " + targetPlan;
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent termListActivity = new Intent(getApplicationContext(), TermListActivity.class);
            termListActivity.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, targetPlan.getId());
            termListActivity.putExtra(DEGREE_PLAN_NAME_BUNDLE_KEY, targetPlan.getName());
            termListActivity.putExtra(DEGREE_PLAN_STUDENT_NAME_BUNDLE_KEY, targetPlan.getStudentName());
            startActivity(termListActivity);
        }
    }
}
