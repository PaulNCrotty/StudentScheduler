package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;

import static android.view.View.generateViewId;

public class CourseDetailsActivity extends StudentSchedulerActivity {

    private static final String TO_BE_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeAssessments";

    public CourseDetailsActivity() {
        super(R.layout.activity_course_detail);
    }

    private Term term;
    private Course course;
    // Transient (all will be lost if course is not stored prior to closing app)
    private List<Assessment> toBeAssessments = new ArrayList<>();

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (toBeAssessments != null) {
            savedInstanceState.putSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY, (Serializable) toBeAssessments);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            toBeAssessments = (ArrayList) savedInstanceState.getSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY);
        }
        init();
        Bundle extras = getIntent().getExtras();
        term = (Term) extras.getSerializable(TERM_OBJECT_BUNDLE_KEY); //TODO pass termId, termStartDate and termEndDate only
        Assessment newAssessment = (Assessment) extras.getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
        if (newAssessment != null) {
            if (toBeAssessments == null) {
                toBeAssessments = new ArrayList<>();
            }
            toBeAssessments.add(newAssessment);
        }

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

            // ensure transient (not yet persisted) assessments if they exist
            if (toBeAssessments.size() > 0) {
                course.getAssessments().addAll(toBeAssessments);
            }
            // Dynamically add assessments if they exist
            if (course.getAssessments().size() > 0) {
                ConstraintLayout layout = findViewById(R.id.assessmentContainer);
                Context context = layout.getContext();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                int viewIndex = 0;
                int bannerConnectorId = layout.getId();
                boolean useStandardStyles = true;
                for (Assessment a : course.getAssessments()) {
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
                    assessmentName.setText(a.getName());
                    layout.addView(assessmentName);

                    assessmentDate.setId(generateViewId());
                    assessmentDate.setText(a.getAssessmentDate());
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

    private CourseStatus getStatusValue() {
        CourseStatus status = CourseStatus.PLANNED;
        if (((RadioButton) findViewById(R.id.plannedStatusButton)).isChecked()) {
            status = CourseStatus.PLANNED;
        }
        else if(((RadioButton) findViewById(R.id.droppedStatusButton)).isChecked()) {
            status = CourseStatus.DROPPED;
        }
        else if (((RadioButton) findViewById(R.id.inProgressStatusButton)).isChecked()) {
            status = CourseStatus.IN_PROGRESS;
        }
        else if(((RadioButton) findViewById(R.id.passedStatusButton)).isChecked()) {
            status = CourseStatus.PASSED;
        }

        return status;
    }


    public void verifyAndSubmitCourse(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        Set<Integer> requiredFields = new HashSet<>();
        requiredFields.add(R.id.courseNameEditText);
        requiredFields.add(R.id.courseCodeEditText);
        requiredFields.add(R.id.courseStartDateEditText);
        requiredFields.add(R.id.courseEndDateEditText);
        requiredFields.add(R.id.instructorFirstNameEditText);
        requiredFields.add(R.id.instructorLastNameEditText);
        requiredFields.add(R.id.instructorPhoneAreaCodeEditText);
        requiredFields.add(R.id.instructorPhonePrefixEditText);
        requiredFields.add(R.id.instructorPhoneSuffixEditText);
        requiredFields.add(R.id.instructorEmailEditText);

        String courseName = getRequiredTextValue(R.id.courseNameEditText, invalidValues);
        String courseCode = getRequiredTextValue(R.id.courseCodeEditText, invalidValues);
        String instructorFirstName = getRequiredTextValue(R.id.instructorFirstNameEditText, invalidValues);
        String instructorLastName = getRequiredTextValue(R.id.instructorLastNameEditText, invalidValues);
        String instructorPhoneAreaCode = getRequiredTextValue(R.id.instructorPhoneAreaCodeEditText, invalidValues);
        String instructorPhonePrefix = getRequiredTextValue(R.id.instructorPhonePrefixEditText, invalidValues);
        String instructorPhoneSuffix = getRequiredTextValue(R.id.instructorPhoneSuffixEditText, invalidValues);
        String instructorEmail = getRequiredTextValue(R.id.instructorEmailEditText, invalidValues);

        int courseStartDate = getRequiredDate(R.id.courseStartDateEditText, invalidValues);
        int courseEndDate = getRequiredDate(R.id.courseEndDateEditText, invalidValues);

        //Make course sure start date is on or before end date
        String errorMessage = null;
        if (courseStartDate > courseEndDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            invalidValues.add(R.id.courseEndDateEditText);
            errorMessage = "The planned course start date must be on or before the course end date.";
        }

        //Make sure dates are within confines of term limits
        int termStartDate = getSecondsSinceEpoch(term.getStartDate());
        int termEndDate = getSecondsSinceEpoch(term.getEndDate());
        if (termStartDate > courseStartDate || termEndDate < courseStartDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            if (errorMessage != null) {
                errorMessage += "\n";  //put a visible space between errors
            }
            errorMessage = "The course start date must be within the planned term dates " + termStartDate + " - " + termEndDate + ".";
        }
        if (termStartDate > courseEndDate || termEndDate < courseEndDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            if (errorMessage != null) {
                errorMessage += "\n";  //put a visible space between errors
            }
            errorMessage = "The course end date must be within the planned term dates " + termStartDate + " - " + termEndDate + ".";
        }

        if (invalidValues.size() > 0) {
            for (Integer id : invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }
            GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment("Invalid Fields", errorMessage);
            errorDialog.show(getSupportFragmentManager(), "courseSubmissionErrors");

            for (Integer id : requiredFields) {
                if (!invalidValues.contains(id)) {
                    findViewById(id).setBackground(null);
                }
            }

        } else {
            long instructorId = repositoryManager.insertInstructor(instructorFirstName, instructorLastName, instructorPhoneAreaCode, instructorPhonePrefix, instructorPhoneSuffix, instructorEmail);
            long courseId = repositoryManager.insertCourse(term.getId(), instructorId, courseName, courseCode, courseStartDate, courseEndDate, getStatusValue().getStatus());

            if (toBeAssessments != null) {
                long[] ids = repositoryManager.insertAssessments(courseId, toBeAssessments);
            }
        }
    }

    public void createAssessmentAction(View view) {
        Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
        assessmentDetailsActivity.putExtra(COURSE_OBJECT_BUNDLE_KEY, course);
        startActivity(assessmentDetailsActivity);
    }

    public void createNoteAction(View view) {
        //TODO how to implement?
    }

    private class ModifyAssessmentAction implements View.OnClickListener {

        ModifyAssessmentAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private final int viewIndex;

        @Override
        public void onClick(View v) {
            Assessment assessment = course.getAssessments().get(this.viewIndex / VIEWS_PER_PLAN);

            //TODO get rid of this once we verify it works well
            String message = "You bonked on " + assessment;
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
            assessmentDetailsActivity.putExtra(COURSE_OBJECT_BUNDLE_KEY, course);
            assessmentDetailsActivity.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
            startActivity(assessmentDetailsActivity);
        }
    }

}
