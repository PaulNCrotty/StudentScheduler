package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static android.view.View.generateViewId;

public class CourseDetailsActivity extends StudentSchedulerActivity {

    private static final int GET_ASSESSMENT_RESULT = 0;
    private static final int GET_NOTE_RESULT = 1;
    private static final String TO_BE_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeAssessments";
    private static final String TO_BE_COURSE_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeCourseNotes";

    public CourseDetailsActivity() {
        super(R.layout.activity_course_detail);
    }

    private Term term;
    private Course course;
    // Transient (all will be lost if course is not stored prior to closing app)
    private List<Assessment> toBeAssessments;
    private List<String> toBeNotes;

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (toBeAssessments != null) {
            savedInstanceState.putSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY, (Serializable) toBeAssessments);
        }
        if(toBeAssessments != null) {
            savedInstanceState.putSerializable(TO_BE_COURSE_NOTES_ARRAY_KEY, (Serializable) toBeNotes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            toBeAssessments = (ArrayList) savedInstanceState.getSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY);
            toBeNotes = (ArrayList) savedInstanceState.getSerializable(TO_BE_COURSE_NOTES_ARRAY_KEY);
        }
        init();
        Bundle extras = getIntent().getExtras();
        term = (Term) extras.getSerializable(TERM_OBJECT_BUNDLE_KEY); //TODO pass termId, termStartDate and termEndDate only
        long courseId = extras.getLong(COURSE_ID_BUNDLE_KEY);
        if (courseId > 0) {
            //we are editing an existing course; grab the details from the persistence store
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
            //TODO remove: this method was from an old approach (but what about flipping the phone/orientation change?)
//            if (toBeAssessments != null && toBeAssessments.size() > 0) {
//                course.getAssessments().addAll(toBeAssessments);
//            }
            // Dynamically add assessments if they exist
            if (course.getAssessments().size() > 0) {
                insertAssessments(course.getAssessments());
            }
        }
    }

    private void insertAssessments(List<Assessment> assessments) {
        ConstraintLayout layout = findViewById(R.id.assessmentContainer);
        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int viewIndex = 0;
        int bannerConnectorId = layout.getId();
        boolean useStandardStyles = true;
        for (Assessment a : assessments) {
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

    private void insertNotes(List<String> notes) {
        ConstraintLayout layout = findViewById(R.id.courseNotesContainer);
        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int viewIndex = 0;  //TODO to be used for accessing notes to be modified later....
        int bannerConnectorId = layout.getId();
        for(String note: notes) {
            //TODO add view for title and an image to delete?
            TextView noteView = new TextView(context);
            noteView.setId(generateViewId());
            noteView.setText(note);
            layout.addView(noteView);

            int noteViewId = noteView.getId();
            constraintSet.connect(noteViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            if(bannerConnectorId == layout.getId()) {
                constraintSet.connect(noteViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            } else {
                constraintSet.connect(noteViewId, ConstraintSet.TOP, bannerConnectorId, ConstraintSet.BOTTOM);
            }
            constraintSet.constrainHeight(noteViewId, ConstraintSet.WRAP_CONTENT);
            constraintSet.setMargin(noteViewId, ConstraintSet.START, marginStart);

            bannerConnectorId = noteViewId;
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

    private CourseStatus getSelectedStatus() {
        CourseStatus status = CourseStatus.PLANNED;
        if (((RadioButton) findViewById(R.id.plannedStatusButton)).isChecked()) {
            status = CourseStatus.PLANNED;
        } else if (((RadioButton) findViewById(R.id.droppedStatusButton)).isChecked()) {
            status = CourseStatus.DROPPED;
        } else if (((RadioButton) findViewById(R.id.inProgressStatusButton)).isChecked()) {
            status = CourseStatus.IN_PROGRESS;
        } else if (((RadioButton) findViewById(R.id.passedStatusButton)).isChecked()) {
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
        CourseStatus selectedStatus = getSelectedStatus();

        long courseStartDate = getRequiredDate(R.id.courseStartDateEditText, invalidValues);
        long courseEndDate = getRequiredDate(R.id.courseEndDateEditText, invalidValues);

        //Make course sure start date is on or before end date
        String errorMessage = verifyDates(invalidValues, courseStartDate, courseEndDate);

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
            long courseId;
            if (course == null) {
                //new course needs to be created
                long instructorId = repositoryManager.insertInstructor(instructorFirstName, instructorLastName, instructorPhoneAreaCode, instructorPhonePrefix, instructorPhoneSuffix, instructorEmail);
                courseId = repositoryManager.insertCourse(term.getId(), instructorId, courseName, courseCode, courseStartDate, courseEndDate, selectedStatus.getStatus());
                //TODO add toast or something?
            } else {
                // run updates
                courseId = course.getId();
                String phoneNumber = instructorPhoneAreaCode + "-" + instructorPhonePrefix + "-" + instructorPhoneSuffix;
                CourseInstructor modifiedInstructor = new CourseInstructor(course.getInstructor().getId(), instructorFirstName, instructorLastName, phoneNumber, instructorEmail);
                if (!course.getInstructor().equals(modifiedInstructor)) {
                    Log.i("UPDATE", "Updating course instructor information from \n" + course.getInstructor() + " \n to \n" + modifiedInstructor);
                    repositoryManager.updateCourseInstructor(modifiedInstructor);
                }
                Course modifiedCourse = new Course(courseId, courseName, courseCode, DateTimeUtil.getDateString(courseStartDate), DateTimeUtil.getDateString(courseEndDate), selectedStatus, null, null, null);
                if (!course.equals(modifiedCourse)) {
                    Log.i("UPDATE", "Updating course information from \n" + course + " \n to \n" + modifiedCourse);
                    repositoryManager.updateCourse(courseId, courseName, courseCode, courseStartDate, courseEndDate, selectedStatus.getStatus());
                }
            }
            if (toBeAssessments != null) {
                long[] ids = repositoryManager.insertAssessments(courseId, toBeAssessments); //TODO what about modifications/upates to courses?
            }
            if (toBeNotes != null) {
                long[] ids = repositoryManager.insertCourseNotes(courseId, toBeNotes); //TODO what about modifications/updates to course notes?
            }
            finish();
        }
    }

    private String verifyDates(Set<Integer> invalidValues, long courseStartDate, long courseEndDate) {
        String errorMessage = null;
        if (courseStartDate > courseEndDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            invalidValues.add(R.id.courseEndDateEditText);
            errorMessage = "The planned course start date must be on or before the course end date.";
        }

        //Make sure dates are within confines of term limits
        long termStartDate = DateTimeUtil.getSecondsSinceEpoch(term.getStartDate());
        long termEndDate = DateTimeUtil.getSecondsSinceEpoch(term.getEndDate());
        if (termStartDate > courseStartDate || termEndDate < courseStartDate) {
            invalidValues.add(R.id.courseStartDateEditText);

            String error = "The course start date must be within the planned term dates " + termStartDate + " - " + termEndDate + ".";
            errorMessage = errorMessage == null ? error : errorMessage + "\n" + error;
        }

        if (termStartDate > courseEndDate || termEndDate < courseEndDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            String error = "The course end date must be within the planned term dates " + termStartDate + " - " + termEndDate + ".";
            errorMessage = errorMessage == null ? error : errorMessage + "\n" + error;
        }
        return errorMessage;
    }

    public void createAssessmentAction(View view) {
        Set<Integer> invalidValues = new HashSet<>();
        long courseStartDate = getRequiredDate(R.id.courseStartDateEditText, invalidValues);
        long courseEndDate = getRequiredDate(R.id.courseEndDateEditText, invalidValues);
        String errorMessage = verifyDates(invalidValues, courseStartDate, courseEndDate);


        if (invalidValues.size() > 0) {
            for (Integer id : invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }
            GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment("Invalid Course Dates", errorMessage);
            errorDialog.show(getSupportFragmentManager(), "courseDateErrors");
        } else {
//            registerForActivityResult(new CreateAssessmentContract(), assessment -> {
//                if (assessment != null) {
//                    if (toBeAssessments == null) {
//                        toBeAssessments = new ArrayList<>();
//                    }
//                    toBeAssessments.add(assessment);
//                }
//            });
            Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
            assessmentDetailsActivity.putExtra(COURSE_START_DATE_BUNDLE_KEY, courseStartDate);
            assessmentDetailsActivity.putExtra(COURSE_END_DATE_BUNDLE_KEY, courseEndDate);
            startActivityForResult(assessmentDetailsActivity, GET_ASSESSMENT_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if(requestCode == GET_ASSESSMENT_RESULT) {
                Assessment newAssessment = (Assessment) data.getExtras().getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
                if (newAssessment != null) {
                    if (toBeAssessments == null) {
                        toBeAssessments = new ArrayList<>();
                    }
                    toBeAssessments.add(newAssessment);
                    if (course != null) {
                        course.getAssessments().add(newAssessment);
                        insertAssessments(course.getAssessments());
                    } else {
                        //otherwise, it's a new course which hasn't been saved yet
                        insertAssessments(toBeAssessments);
                    }
                }
            } else if (requestCode == GET_NOTE_RESULT) {
                String newNote = data.getExtras().getString(COURSE_NOTE_BUNDLE_KEY);
                if(newNote != null) {
                    if(toBeNotes == null) {
                        toBeNotes = new ArrayList<>();
                    }
                    toBeNotes.add(newNote);
                }
                if(course != null) {
                    course.getCourseNotes().add(newNote);
                    insertNotes(course.getCourseNotes());
                } else {
                    insertNotes(toBeNotes);
                }
            }
        }
    }

    //TODO add similar functionality for notes....
    private class ModifyAssessmentAction implements View.OnClickListener {

        ModifyAssessmentAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private final int viewIndex;

        @Override
        public void onClick(View v) {

            Assessment assessment;
            if (course != null) {
                assessment = course.getAssessments().get(this.viewIndex / VIEWS_PER_PLAN);
            } else {
                assessment = toBeAssessments.get(this.viewIndex / VIEWS_PER_PLAN);
            }

            //TODO this is not very DRY... should probably create an nested class that manages all of this
            Set<Integer> invalidValues = new HashSet<>();
            //always take current dates as those will be what we will save (eventually)
            long courseStartDate = getRequiredDate(R.id.courseStartDateEditText, invalidValues);
            long courseEndDate = getRequiredDate(R.id.courseEndDateEditText, invalidValues);
            String errorMessage = verifyDates(invalidValues, courseStartDate, courseEndDate);


            if (invalidValues.size() > 0) {
                for (Integer id : invalidValues) {
                    findViewById(id).setBackground(errorBorder);
                }
                GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment("Invalid Course Dates", errorMessage);
                errorDialog.show(getSupportFragmentManager(), "courseDateErrors");
            } else {
                //TODO get rid of this once we verify it works well
                Toast.makeText(getApplicationContext(), "You bonked on " + assessment, Toast.LENGTH_SHORT).show();
                Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
                assessmentDetailsActivity.putExtra(COURSE_START_DATE_BUNDLE_KEY, courseStartDate);
                assessmentDetailsActivity.putExtra(COURSE_END_DATE_BUNDLE_KEY, courseEndDate);
                assessmentDetailsActivity.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
                startActivity(assessmentDetailsActivity);
            }
        }
    }

    public void createNoteAction(View view) {
        startActivityForResult(new Intent(getApplicationContext(), CourseNoteActivity.class), GET_NOTE_RESULT);
    }

}
