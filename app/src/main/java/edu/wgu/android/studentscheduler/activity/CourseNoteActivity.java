package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;

public class CourseNoteActivity extends StudentSchedulerActivity {

    CourseNoteActivity() {
        super(R.layout.activity_course_note);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String note = extras.getString(COURSE_NOTE_BUNDLE_KEY);  // TODO will probably change to an object to contain a title? Maybe not... IDK

        if(note != null) {
            EditText noteEditText = (EditText) findViewById(R.id.courseNoteEditText);
            noteEditText.setText(note);
        }
    }

    public void verifyAndAddNoteToCourse(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        Set<Integer> requiredFields = new HashSet<>();
        requiredFields.add(R.id.courseNoteEditText);

        String newNote = getRequiredTextValue(R.id.courseNoteEditText, invalidValues);
        if(invalidValues.size() > 0) {
            for(Integer id: invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }

            for(Integer id: requiredFields) {
                if(!invalidValues.contains(id)) {
                    findViewById(id).setBackground(null);
                }
            }
        } else {
            Intent intent = getIntent();
            intent.putExtra(COURSE_NOTE_BUNDLE_KEY, newNote);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}
