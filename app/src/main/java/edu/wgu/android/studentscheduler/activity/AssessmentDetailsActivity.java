package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import edu.wgu.android.studentscheduler.R;

public class AssessmentDetailsActivity extends AppCompatActivity {

    public AssessmentDetailsActivity() {
        super(R.layout.activity_assessment_detail);
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
}
