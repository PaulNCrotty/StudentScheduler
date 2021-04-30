package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.persistence.dao.DegreePlanDao;

public class DegreePlanListActivity extends StudentSchedulerActivity {

    public DegreePlanListActivity() {
        super(R.layout.activity_degree_plan_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<DegreePlanDao> planDaos = repositoryManager.getBasicDegreePlanDataForAllPlans();
    }

    public void verifyAndSubmitPlan(View view) {
        //TODO implement
    }
}
