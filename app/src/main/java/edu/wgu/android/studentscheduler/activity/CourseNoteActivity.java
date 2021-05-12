package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.CourseNote;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

public class CourseNoteActivity extends StudentSchedulerActivity {

    private CourseNote originalNote;

    public CourseNoteActivity() {
        super(R.layout.activity_course_note);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Serializable sNote = extras.getSerializable(COURSE_NOTE_BUNDLE_KEY);
            if (sNote instanceof CourseNote) {
                originalNote = (CourseNote) sNote;
                TextView modifiedDateText = findViewById(R.id.modifiedDateText);
                String modifiedDate = originalNote.getModifiedDate() == null ? originalNote.getCreatedDate() : originalNote.getModifiedDate();
                modifiedDateText.setText(modifiedDate);

                EditText titleEditText = findViewById(R.id.courseNoteTitleEditText);
                EditText noteBodyEditText = findViewById(R.id.courseNoteEditText);

                titleEditText.setText(originalNote.getTitle());
                noteBodyEditText.setText(originalNote.getNoteBody());
            }
        }
    }

    public void verifyAndAddNoteToCourse(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        Set<Integer> requiredFields = new HashSet<>();
        requiredFields.add(R.id.courseNoteTitleEditText);
        requiredFields.add(R.id.courseNoteEditText);

        String noteBody = getRequiredTextValue(R.id.courseNoteEditText, invalidValues);
        String noteTitle = getRequiredTextValue(R.id.courseNoteTitleEditText, invalidValues);
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
            CourseNote note;
            Intent intent = getIntent();
            if(originalNote == null) {
                //it's a new  note
                String createdDate = DateTimeUtil.getDateString(DateTimeUtil.getSecondsSinceEpoch());
                note = new CourseNote(0, noteTitle, noteBody, createdDate, null);
            } else {
                note = new CourseNote(0, noteTitle, noteBody, null, null);
                //only body and title are used to calculate equality at this point
                intent.putExtra(IS_MODIFIED, originalNote.equals(note));
            }
            intent.putExtra(COURSE_NOTE_BUNDLE_KEY, note);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}
