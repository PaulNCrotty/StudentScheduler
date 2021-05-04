package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;

import static android.view.View.generateViewId;

public class CourseDetailsActivity extends StudentSchedulerActivity {

    public CourseDetailsActivity() {
        super(R.layout.activity_course_detail);
    }

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        long termId = extras.getLong(TERM_ID_BUNDLE_KEY);
        long courseId = extras.getLong(COURSE_ID_BUNDLE_KEY);
        if (courseId > 0) {
            course = repositoryManager.getCourseDetails(courseId);
            ((EditText) findViewById(R.id.courseNameEditText)).setText(course.getCourseName());
            ((EditText) findViewById(R.id.courseCodeEditText)).setText(course.getCourseCode());
            ((EditText) findViewById(R.id.courseStartDateEditText)).setText(course.getStartDate());
            ((EditText) findViewById(R.id.courseEndDateEditText)).setText(course.getEndDate());

            setStatusButton(course);

            CourseInstructor instructor = course.getInstructor();
            ((EditText) findViewById(R.id.instructorFirstNameEditText)).setText(instructor.getFirstName());
            ((EditText) findViewById(R.id.instructorLastNameEditText)).setText(instructor.getLastName());
            String[] phone = instructor.getPhoneNumber().split("-");
            ((EditText) findViewById(R.id.instructorPhoneAreaCodeEditText)).setText(phone[0]);
            ((EditText) findViewById(R.id.instructorPhonePrefixEditText)).setText(phone[1]);
            ((EditText) findViewById(R.id.instructorPhoneSuffixEditText)).setText(phone[phone.length - 1]);
            ((EditText) findViewById(R.id.instructorEmailEditText)).setText(instructor.getEmail());

            // Dynamically add assessments if they exist
            if (course.getAssessments().size() > 0) {
                ConstraintLayout layout = findViewById(R.id.assessmentContainer);
                Context context = layout.getContext();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                int viewIndex = 0;
                int bannerConnectorId = layout.getId();
                boolean useStandardStyles = true;
                for (Assessment assessment : course.getAssessments()) {
                    //declare views and prep for basic color styles
                    TextView banner;
                    TextView assessmentName;
                    TextView assessmentDate;
                    if (useStandardStyles) {
                        banner = new TextView(context, null, 0, R.style.listOptionBanner);
                        assessmentName = new TextView(context, null, 0, R.style.listOptionDetails);
                        assessmentDate = new TextView(context, null, 0, R.style.listOptionDates);
                    } else {
                        banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                        assessmentName = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                        assessmentDate = new TextView(context, null, 0, R.style.listOptionDatesAlt);
                    }
                    //set content
                    banner.setId(generateViewId());
                    banner.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
                    layout.addView(banner);

                    assessmentName.setId(generateViewId());
                    assessmentName.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
                    assessmentName.setText(assessment.getName());
                    layout.addView(assessmentName);

                    assessmentDate.setId(generateViewId());
                    assessmentDate.setText(assessment.getAssessmentDate());
                    assessmentDate.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
                    layout.addView(assessmentDate);

                    // add constraints
                    addBannerConstraints(constraintSet, layout.getId(), banner.getId(), bannerConnectorId);
                    addPlanNamesConstraints(constraintSet, assessmentName.getId(), banner.getId());
                    addModifiedDatesConstraints(constraintSet, assessmentDate.getId(), banner.getId());

                    //prep for next iteration
                    bannerConnectorId = banner.getId();
                    useStandardStyles = !useStandardStyles;
                }

                constraintSet.applyTo(layout);
            }
        }
    }

    private void setStatusButton(Course course) {
        CourseStatus status = course.getStatus();
        switch (status) {
            case PLANNED:
                ((RadioButton) findViewById(R.id.plannedStatusButton)).setChecked(true);
                break;
            case DROPPED:
                ((RadioButton) findViewById(R.id.droppedStatusButton)).setChecked(true);
                break;
            case IN_PROGRESS:
                ((RadioButton) findViewById(R.id.inProgressStatusButton)).setChecked(true);
                break;
            case PASSED:
                ((RadioButton) findViewById(R.id.passedStatusButton)).setChecked(true);
                break;
            case ENROLLED:
            case FAILED:
            default:
                Toast.makeText(this, status.getStatus() + " is not yet supported. How did we get here?", Toast.LENGTH_SHORT).show();
        }
    }

    private class ModifyAssessmentAction implements View.OnClickListener {

        ModifyAssessmentAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private int viewIndex;

        @Override
        public void onClick(View v) {
            Assessment assessment = course.getAssessments().get(this.viewIndex / VIEWS_PER_PLAN);
            String message = "You bonked on " + assessment;
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
            assessmentDetailsActivity.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
            startActivity(assessmentDetailsActivity);
        }
    }

}
