package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.persistence.MockDegreePlanRepository;

/**
 *
 * Generally, your fragment must be embedded within an AndroidX FragmentActivity to contribute a
 * portion of UI to that activity's layout. FragmentActivity is the base class for AppCompatActivity,
 * so if you're already subclassing AppCompatActivity to provide backward compatibility in your app,
 * then you do not need to change your activity base class.
 * (https://developer.android.com/guide/fragments/create#java)
 */
public class MainActivity extends StudentSchedulerActivity {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    public void createNewPlan(View view) {
        startActivity(new Intent(this, DegreePlanCreationActivity.class));
    }

    public void loadExistingPlans(View view) {
        startActivity(new Intent(this, DegreePlanListActivity.class));
    }

    public void generatePlanForTesting(View view) {
        MockDegreePlanRepository degreePlanRepository = new MockDegreePlanRepository();
        DegreePlan degreePlanData = degreePlanRepository.getDegreePlanData();
        repositoryManager.insertMockDegreePlan(degreePlanData);
    }

}