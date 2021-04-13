package edu.wgu.android.studentscheduler;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;
import java.util.List;

import edu.wgu.android.studentscheduler.domain.Term;
import edu.wgu.android.studentscheduler.domain.TermStatus;
import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;

/**
 * Generally, your fragment must be embedded within an AndroidX FragmentActivity to contribute a
 * portion of UI to that activity's layout. FragmentActivity is the base class for AppCompatActivity,
 * so if you're already subclassing AppCompatActivity to provide backward compatibility in your app,
 * then you do not need to change your activity base class.
 * (https://developer.android.com/guide/fragments/create#java)
 */
public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * In the previous example, note that the fragment transaction is only created when
         * savedInstanceState is null. This is to ensure that the fragment is added only once, when
         * the activity is first created. When a configuration change occurs and the activity is
         * recreated, savedInstanceState is no longer null, and the fragment does not need to be
         * added a second time, _as the fragment is automatically restored_ from the
         * savedInstanceState. (see https://developer.android.com/guide/fragments/create)
         *
         * Other really useful information: https://developer.android.com/guide/fragments/fragmentmanager
         */
//        if(savedInstanceState == null) {
//            List<Term> termData = getTermData();
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.term_container_view, new TermFragment(termData.get(0)))
//                    .add(R.id.term_container_view, new TermFragment(termData.get(1)))
//                    .add(R.id.term_container_view, new TermFragment(termData.get(2)))
//                    .setReorderingAllowed(true)
//                    .commit();
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment termContainerFragment = fragmentManager.findFragmentById(R.id.term_container_fragment);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

//    List<Bundle> getTermData() {
//        List<Bundle> termData = new ArrayList<>();
//
//        //TODO populate with data from SQLite persistence store
//        Bundle termOneBundle = new Bundle();
//        termOneBundle.putString("termName", "Term One");
//        termOneBundle.putString("termStartDate", "2021-01-01");
//        termOneBundle.putString("termEndDate", "2021-06-30");
//
//        termData.add(termOneBundle);
//
//        return termData;
//    }

    List<Term> getTermData() {
        return Arrays.asList(
                new Term("Term One", "2020-07-01", "2020-12-31", null, TermStatus.PAST_COMPLETE),
                new Term("Term Two", "2021-01-01", "2021-06-30", null, TermStatus.CURRENT),
                new Term("Term Three", "2021-07-01", "2021-12-31", null, TermStatus.FUTURE_UNAPPROVED)
        );
    }
}