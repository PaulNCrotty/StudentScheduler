package edu.wgu.android.studentscheduler;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment termContainerFragment = fragmentManager.findFragmentById(R.id.term_list_fragment);

        if(termContainerFragment == null) {
            System.out.println("Creating new fragment container for Terms");
            fragmentManager.beginTransaction()
                    .add(R.id.term_list_fragment, termContainerFragment)
                    .commit();
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }
}