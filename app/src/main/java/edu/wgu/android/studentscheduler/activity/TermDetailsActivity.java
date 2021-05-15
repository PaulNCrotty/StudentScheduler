package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.domain.term.TermStatus;
import edu.wgu.android.studentscheduler.persistence.contract.DegreePlanContract;
import edu.wgu.android.studentscheduler.widget.IndexedCheckBox;

import static android.view.View.generateViewId;

public class TermDetailsActivity extends StudentSchedulerActivity {

    private static int CREATE_NEW_COURSE_REQUEST = 0;
    private static int MODIFY_COURSE_REQUEST = 1;

    private static String TERM_COURSE_ARRAY_BUNDLE_KEY = "edu.wgu.android.studentscheduler.activity.termCourses";

    private long degreePlanId;
    private Term term;
    private List<Course> termCourses;

    public TermDetailsActivity() {
        super(R.layout.activity_term_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        Bundle extras = getIntent().getExtras();
        degreePlanId = (long) extras.get(DEGREE_PLAN_ID_BUNDLE_KEY);

        term = (Term) extras.get(TERM_OBJECT_BUNDLE_KEY);
        if (term != null) {
            ((EditText) findViewById(R.id.termNameEditText)).setText(term.getName());
            ((EditText) findViewById(R.id.termStartDateEditText)).setText(term.getStartDate());
            ((EditText) findViewById(R.id.termEndDateEditText)).setText(term.getEndDate());
            setSelectedStatus(term.getStatus());

            termCourses = getTermCourses(term.getId());
            insertCourses(termCourses);
        }
    }

    private void clearCourses() {
        ConstraintLayout layout = findViewById(R.id.courseContainer);
        layout.removeAllViews();
    }

    private void insertCourses(List<Course> courses) {
        //dynamically set rows for courses
        ConstraintLayout layout = findViewById(R.id.courseContainer);
        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int viewIndex = 0;
        int connectionId = layout.getId();
        boolean useStandardStyles = true;
        for (Course c : courses) {
            //High-level component one details
            TextView banner;
            IndexedCheckBox removeIcon = new IndexedCheckBox(context);
            TextView title;
            TextView dates;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                removeIcon.setBackgroundColor(orangeColor);
                title = new TextView(context, null, 0, R.style.listOptionDetails);
                dates = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                title = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                dates = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }

            banner.setId(generateViewId());
            banner.setOnClickListener(new ModifyCourseAction(viewIndex++));
            layout.addView(banner);

            removeIcon.setId(generateViewId());
            removeIcon.setViewIndex(viewIndex++);
            removeIcon.setChecked(false);
            layout.addView(removeIcon);

            title.setId(generateViewId());
            title.setOnClickListener(new ModifyCourseAction(viewIndex++));
            title.setText(getString(R.string.course_title, c.getCourseCode(), c.getCourseName()));
            layout.addView(title);

            dates.setId(generateViewId());
            dates.setOnClickListener(new ModifyCourseAction(viewIndex++));
            dates.setText(c.getStartDate());
            layout.addView(dates);

            // add constraints
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), removeIcon.getId(), connectionId);
            addIconConstraints(constraintSet, removeIcon.getId(), banner.getId());
            addPlanNamesConstraints(constraintSet, title.getId(), banner.getId());
            addModifiedDatesConstraints(constraintSet, dates.getId(), banner.getId());

            connectionId = banner.getId();
            useStandardStyles = !useStandardStyles;

        }

        constraintSet.applyTo(layout);
    }

    TermStatus getSelectedStatus() {
        TermStatus status = TermStatus.FUTURE_UNAPPROVED;
        if (((RadioButton) findViewById(R.id.plannedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_UNAPPROVED;
        } else if (((RadioButton) findViewById(R.id.approvedStatusButton)).isChecked()) {
            status = TermStatus.FUTURE_APPROVED;
        } else if (((RadioButton) findViewById(R.id.enrolledStatusButton)).isChecked()) {
            status = TermStatus.CURRENT;
        } else if (((RadioButton) findViewById(R.id.incompleteStatusButton)).isChecked()) {
            status = TermStatus.PAST_INCOMPLETE;
        } else if (((RadioButton) findViewById(R.id.completedStatusButton)).isChecked()) {
            status = TermStatus.PAST_COMPLETE;
        }
        return status;
    }

    void setSelectedStatus(TermStatus status) {
        switch (status) {
            case FUTURE_UNAPPROVED:
                ((RadioButton) findViewById(R.id.plannedStatusButton)).setChecked(true);
                break;
            case FUTURE_APPROVED:
                ((RadioButton) findViewById(R.id.approvedStatusButton)).setChecked(true);
                break;
            case CURRENT:
                ((RadioButton) findViewById(R.id.enrolledStatusButton)).setChecked(true);
                break;
            case PAST_INCOMPLETE:
                ((RadioButton) findViewById(R.id.incompleteStatusButton)).setChecked(true);
                break;
            case PAST_COMPLETE:
                ((RadioButton) findViewById(R.id.completedStatusButton)).setChecked(true);
                break;
        }
    }

    private List<Course> getTermCourses(long termId) {
        return repositoryManager.getTermCourses(termId);
    }

    public void addCourseToTerm(View view) {
        Intent courseDetailsActivity = new Intent(getApplicationContext(), CourseDetailsActivity.class);
        courseDetailsActivity.putExtra(TERM_OBJECT_BUNDLE_KEY, term);
        startActivityForResult(courseDetailsActivity, CREATE_NEW_COURSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CREATE_NEW_COURSE_REQUEST || requestCode == MODIFY_COURSE_REQUEST) {
                termCourses = getTermCourses(term.getId()); //silver bullet (less efficient?)
                clearCourses();
                insertCourses(termCourses);
            }
        }
    }

    public void saveTerm(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        Set<Integer> validValues = new HashSet<>();

        String name = getRequiredTextValue(R.id.termNameEditText, invalidValues, validValues);
        long startDate = getRequiredDate(R.id.termStartDateEditText, invalidValues);
        long endDate = getRequiredDate(R.id.termEndDateEditText, invalidValues);

        TermStatus selectedStatus = getSelectedStatus();

        if (startDate > endDate) {
            invalidValues.add(R.id.termStartDateEditText);
            invalidValues.add(R.id.termEndDateEditText);
        }

        if (invalidValues.size() > 0) {
            for (Integer i : invalidValues) {
                findViewById(i).setBackground(errorBorder);
            }
            Toast.makeText(this, "Fix your input before saving", Toast.LENGTH_SHORT).show();
        } else {
            if (term == null) {
                repositoryManager.insertTerm(degreePlanId, name, startDate, endDate, selectedStatus.getStatus());
            } else {
                repositoryManager.updateTerm(term.getId(), name, startDate, endDate, selectedStatus.getStatus());
            }
            setResult(RESULT_OK);
            finish();
        }

    }
    
    public void deleteSelectedCourses(View view) {
        List<Integer> indices = new ArrayList<>();
        ConstraintLayout layout = findViewById(R.id.courseContainer);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof IndexedCheckBox) {
                IndexedCheckBox checkBox = (IndexedCheckBox) child;
                if (checkBox.isChecked()) {
                    indices.add(checkBox.getViewIndex());
                }
            }
        }
        
        if(indices.size() > 0) {
            List<Long> coursesToDelete = new ArrayList<>(indices.size());
            for(Integer i: indices) {
                Course c = termCourses.get(i / VIEWS_PER_ROW);
                coursesToDelete.add(c.getId());
            }
            repositoryManager.deleteEntries(coursesToDelete, DegreePlanContract.Course.TABLE_NAME);
            termCourses = getTermCourses(term.getId());
            clearCourses();
            insertCourses(termCourses);
        } else {
            Toast.makeText(this, "Please check a course or series of courses to delete", Toast.LENGTH_LONG).show();
        }
        
    }

    private class ModifyCourseAction implements View.OnClickListener {

        int viewIndex;

        ModifyCourseAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        @Override
        public void onClick(View v) {
            int index = this.viewIndex / VIEWS_PER_ROW;
            Course course = termCourses.get(index);
            String toastMessage = "You bonked on " + course.getCourseName();
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();

            Intent courseDetailsActivity = new Intent(getApplicationContext(), CourseDetailsActivity.class);
            courseDetailsActivity.putExtra(TERM_OBJECT_BUNDLE_KEY, term);
            courseDetailsActivity.putExtra(COURSE_ID_BUNDLE_KEY, course.getId());
            startActivityForResult(courseDetailsActivity, MODIFY_COURSE_REQUEST);

        }
    }

}
