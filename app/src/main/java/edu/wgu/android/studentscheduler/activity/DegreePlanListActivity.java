package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
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

    private static final int MAX_RECENT_TO_LIST = 3;
    private static final int VIEWS_PER_PLAN = 3;

    //Have to be injected via constraints as they are not honored from TextView styles
    private int bannerHeight;
    private int marginStart;
    private int marginEnd;

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
        Resources resources = init();  //required to instantiated and use standard colors
        bannerHeight = resources.getDimensionPixelSize(R.dimen.text_view_degree_plan_list_layout_height);
        marginStart = resources.getDimensionPixelSize(R.dimen.text_view_degree_plan_list_layout_marginStart);
        marginEnd = resources.getDimensionPixelSize(R.dimen.text_view_degree_plan_list_layout_marginEnd);

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
            TextView banner;
            TextView planNames;
            TextView modifiedDates;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                planNames = new TextView(context, null, 0, R.style.listOptionDetails);
                modifiedDates = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                planNames = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                modifiedDates = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }
            //set content
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
            addBannerConstraints(constraintSet, recentPlansContainer.getId(), banner.getId(), bannerConnectorId);
            addPlanNamesConstraints(constraintSet, planNames.getId(), banner.getId());
            addModifiedDatesConstraints(constraintSet, modifiedDates.getId(), banner.getId());

            //prep for next iteration
            bannerConnectorId = banner.getId();
            useStandardStyles = !useStandardStyles;
        }

        recentPlansConstraints.applyTo(recentPlansContainer);
        remainingPlansConstraints.applyTo(remainingPlansContainer);

    }

    private void addBannerConstraints(ConstraintSet constraintSet, int containerId, int constrainedViewId, int connectorId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        if (connectorId == containerId) {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.TOP);
        } else {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.BOTTOM);
        }
        //height is not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, bannerHeight);
    }

    private void addPlanNamesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, bannerId, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
        constraintSet.setMargin(constrainedViewId, ConstraintSet.START, marginStart);
    }

    private void addModifiedDatesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
        constraintSet.setMargin(constrainedViewId, ConstraintSet.END, marginEnd);
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

    private class ModifyDegreePlanAction implements View.OnClickListener {

        ModifyDegreePlanAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private int viewIndex;

        @Override
        public void onClick(View v) {
            int index = this.viewIndex / VIEWS_PER_PLAN;
            String message = "You bonked on " + planDAOs.get(index);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent degreePlanActivity = new Intent(getApplicationContext(), DegreePlanActivity.class);
            degreePlanActivity.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, planDAOs.get(index).getId());
            startActivity(degreePlanActivity);
        }
    }
}
