package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;

import edu.wgu.android.studentscheduler.R;

/**
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

    //TODO remove when done....
    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}