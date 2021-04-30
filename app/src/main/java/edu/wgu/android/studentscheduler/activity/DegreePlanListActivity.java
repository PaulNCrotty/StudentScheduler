package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;

public class DegreePlanListActivity extends StudentSchedulerActivity {

    private List<DegreePlan> recentlyModifiedPlans;
    private List<DegreePlan> remainingDegreePlans;

    public DegreePlanListActivity() {
        super(R.layout.activity_degree_plan_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<DegreePlanDao> planDAOs = repositoryManager.getBasicDegreePlanDataForAllPlans();


    }

    public void verifyAndSubmitPlan(View view) {
        //TODO implement
    }
}
