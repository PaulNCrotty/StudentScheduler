package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static android.view.View.generateViewId;

public class DegreePlanListActivity extends StudentSchedulerActivity {

    private static final int MAX_RECENT_TO_LIST = 3;
    private static final int VIEWS_PER_PLAN = 3;

//    private List<DegreePlan> recentlyModifiedPlans;
//    private List<DegreePlan> remainingDegreePlans;

    public DegreePlanListActivity() {
        super(R.layout.activity_degree_plan_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<DegreePlanDao> planDAOs = repositoryManager.getBasicDegreePlanDataForAllPlans();  //TODO shouldn't be calling a repo from a view layer
        //put them in reverse chronological order so newly worked on plans appear first (original sorting done in repo layer)
        Collections.reverse(planDAOs);
        int numberOfPlans = planDAOs.size();
//        recentlyModifiedPlans = numberOfPlans >= MAX_RECENT_TO_LIST ? new ArrayList<>(MAX_RECENT_TO_LIST) : new ArrayList<>(numberOfPlans);
//        remainingDegreePlans = numberOfPlans > MAX_RECENT_TO_LIST ? new ArrayList<>(numberOfPlans - MAX_RECENT_TO_LIST) : null;
        boolean useStandardStyles = true;

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
        boolean isFirstRemainingDegreePlan = true;
        for (int i = 0; i < numberOfPlans; i++) {
            DegreePlanDao d = planDAOs.get(i);
            if (i < MAX_RECENT_TO_LIST) {
                TextView banner;
                TextView planNames;
                TextView modifiedDates;
                if (useStandardStyles) {
                    banner = new TextView(recentPlansContext, null, R.style.listOptionBanner);
                    planNames = new TextView(recentPlansContext, null, R.style.listOptionDetails);
                    modifiedDates = new TextView(recentPlansContext, null, R.style.listOptionDates);
                } else {
                    banner = new TextView(recentPlansContext, null, R.style.listOptionBannerAlt);
                    planNames = new TextView(recentPlansContext, null, R.style.listOptionDetailsAlt);
                    modifiedDates = new TextView(recentPlansContext, null, R.style.listOptionDatesAlt);
                }
                //set content
                banner.setId(generateViewId());
                banner.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));

                planNames.setId(generateViewId());
                planNames.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
                planNames.setText(DateTimeUtil.getDateString(d.getLastModified()));

                modifiedDates.setId(generateViewId());
                modifiedDates.setText(getString(R.string.degree_plan_basic_description, d.getStudentName(), d.getName()));
                modifiedDates.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));

                // add constraints
                recentPlansConstraints.connect(banner.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                if(bannerConnectorId == recentPlansContainer.getId()) {
                    recentPlansConstraints.connect(banner.getId(), ConstraintSet.TOP, bannerConnectorId, ConstraintSet.TOP);
                } else {
                    recentPlansConstraints.connect(banner.getId(), ConstraintSet.TOP, bannerConnectorId, ConstraintSet.BOTTOM);
                }

                recentPlansConstraints.connect(planNames.getId(), ConstraintSet.TOP, banner.getId(), ConstraintSet.TOP);
                recentPlansConstraints.connect(planNames.getId(), ConstraintSet.BOTTOM, banner.getId(), ConstraintSet.BOTTOM);
                recentPlansConstraints.connect(planNames.getId(), ConstraintSet.START, banner.getId(), ConstraintSet.START);

                recentPlansConstraints.connect(modifiedDates.getId(), ConstraintSet.TOP, banner.getId(), ConstraintSet.TOP);
                recentPlansConstraints.connect(modifiedDates.getId(), ConstraintSet.BOTTOM, banner.getId(), ConstraintSet.BOTTOM);
                recentPlansConstraints.connect(modifiedDates.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                bannerConnectorId = banner.getId();

            } else {
                if(isFirstRemainingDegreePlan) {
                    useStandardStyles = true; //reset pattern for new section ('remaining degree plans')
                    isFirstRemainingDegreePlan = false;
                }

                TextView banner;
                TextView planNames;
                TextView modifiedDates;
                if (useStandardStyles) {
                    banner = new TextView(remainingPlansContext, null, R.style.listOptionBanner);
                    planNames = new TextView(remainingPlansContext, null, R.style.listOptionDetails);
                    modifiedDates = new TextView(remainingPlansContext, null, R.style.listOptionDates);
                } else {
                    banner = new TextView(remainingPlansContext, null, R.style.listOptionBannerAlt);
                    planNames = new TextView(remainingPlansContext, null, R.style.listOptionDetailsAlt);
                    modifiedDates = new TextView(remainingPlansContext, null, R.style.listOptionDatesAlt);
                }
                banner.setId(generateViewId());
                banner.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
                modifiedDates.setId(generateViewId());
                modifiedDates.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
                planNames.setId(generateViewId());
                planNames.setOnClickListener(new ModifyDegreePlanAction(viewIndex++));
            }
            useStandardStyles = !useStandardStyles;
        }


    }

    private class ModifyDegreePlanAction implements View.OnClickListener {

        ModifyDegreePlanAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private int viewIndex;

        @Override
        public void onClick(View v) {

        }
    }
}
