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
import edu.wgu.android.studentscheduler.util.CollectionUtil;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;
import edu.wgu.android.studentscheduler.widget.IndexedCheckBox;

import static android.view.View.generateViewId;


/***
 * Bugs and missing features:
 * when one toggles the screen, any recently added assessments (or notes?) are still in  memory thanks to the onSaveInstanceState...
 * but they do not get redrawn on the screen. Need to redraw them ... once.
 *   After submitting with this approach.... only one of the assessments was saved... twice
 *
 * Cartesian join when multiple notes and assessments exist.
 *
 * No way to delete plans, terms, courses, assessments, or notes just yet
 *
 * Regular DegreePlanView issues:
 *  When submitting course, it takes you back to the degree plan view but the course is not visible
 *  Once one course is created, no option exists to create another course....
 *
 */
public class CourseDetailsActivity extends StudentSchedulerActivity {
    //TODO override for now... until we get all UIs in sync with 4 views per row
    static final int VIEWS_PER_ROW = 4;

    private static final int GET_ASSESSMENT_RESULT = 0;
    private static final int GET_NOTE_RESULT = 1;
    private static final int MODIFY_ASSESSMENT_RESULT = 2;
    private static final int MODIFY_NOTE_RESULT = 3;
    private static final int DELETE_ASSESSMENT_KEY = 4;
    private static final String ORIGINAL_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.originalAssessments";
    private static final String ORIGINAL_COURSE_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.originalNotes";
    private static final String TO_BE_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeAssessments";
    private static final String TO_BE_DELETED_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeDeletedAssessments";
    private static final String TO_BE_COURSE_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeCourseNotes";

    public CourseDetailsActivity() {
        super(R.layout.activity_course_detail);
    }

    private Term term;
    private Course course;
    private List<Assessment> originalAssessments;   //used to track and compare if assessments have changed
    private List<String> originalNotes;             //used to track and compare if notes have changed
    private List<Assessment> toBeAssessments;       //transient assessments; must be saved at end of activity
    private List<Assessment> toBeDeletedAssessment; //transient assessments to be deleted; must be saved at end of activity for deletion to be permanent
    private List<String> toBeNotes;                 //transient assessments; must be saved at end of activity

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (toBeAssessments != null) {
            savedInstanceState.putSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY, (Serializable) toBeAssessments);
        }
        if (toBeNotes != null) {
            savedInstanceState.putSerializable(TO_BE_COURSE_NOTES_ARRAY_KEY, (Serializable) toBeNotes);
        }
        if (course != null) {
            savedInstanceState.putSerializable(COURSE_OBJECT_BUNDLE_KEY, course);
        }
        if (toBeDeletedAssessment != null) {
            savedInstanceState.putSerializable(TO_BE_DELETED_ASSESSMENTS_ARRAY_KEY, (Serializable) toBeDeletedAssessment);
        }

        savedInstanceState.putSerializable(ORIGINAL_ASSESSMENTS_ARRAY_KEY, (Serializable) originalAssessments);
        savedInstanceState.putSerializable(ORIGINAL_COURSE_NOTES_ARRAY_KEY, (Serializable) originalNotes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Serializable nAssessments = savedInstanceState.getSerializable(TO_BE_ASSESSMENTS_ARRAY_KEY);
            if (nAssessments instanceof ArrayList) {
                toBeAssessments = (ArrayList<Assessment>) nAssessments;
            }

            Serializable notes = savedInstanceState.getSerializable(TO_BE_COURSE_NOTES_ARRAY_KEY);
            if (notes instanceof ArrayList) {
                toBeNotes = (ArrayList<String>) notes;
            }

            Serializable oAssessments = savedInstanceState.getSerializable(ORIGINAL_ASSESSMENTS_ARRAY_KEY);
            if (oAssessments instanceof ArrayList) {
                originalAssessments = (ArrayList<Assessment>) oAssessments;
            }

            Serializable dAssessments = savedInstanceState.getSerializable(TO_BE_DELETED_ASSESSMENTS_ARRAY_KEY);
            if (dAssessments instanceof ArrayList) {
                toBeDeletedAssessment = (ArrayList<Assessment>) dAssessments;
            }

            course = (Course) savedInstanceState.getSerializable(COURSE_OBJECT_BUNDLE_KEY);
        }

        init();
        Bundle extras = getIntent().getExtras();
        term = (Term) extras.getSerializable(TERM_OBJECT_BUNDLE_KEY); //TODO pass termId, termStartDate and termEndDate only
        long courseId = extras.getLong(COURSE_ID_BUNDLE_KEY);
        if (courseId > 0) {
            //we are editing an existing course; grab the details from the persistence store
            if (course == null) {
                course = repositoryManager.getCourseDetails(courseId);
                course.getCourseNotes().addAll(repositoryManager.getCourseNotes(courseId));
            }

            if (originalAssessments == null) {
                // should only be set on first creation (not on orientation changes, etc...)
                //create a copy... otherwise its just an alias
                Log.i("COURSE_DETAILS", "setting original assessments");
                originalAssessments = new ArrayList<>(course.getAssessments());
            }

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

            // Dynamically add assessments and notes to layout if they exist
            // Note: copyAndAdd returns a deep copy of the collections
            List<Assessment> assessmentsToDisplay = CollectionUtil.copyAndAdd(course.getAssessments(), toBeAssessments);
            if (assessmentsToDisplay.size() > 0) {
                insertAssessments(assessmentsToDisplay);
            }

            List<String> notesToDisplay = CollectionUtil.copyAndAdd(course.getCourseNotes(), toBeNotes);
            if (notesToDisplay.size() > 0) {
                insertNotes(notesToDisplay);
            }
        }
    }

    private void clearAssessments() {
        ConstraintLayout layout = findViewById(R.id.assessmentContainer);
        layout.removeAllViews();
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
            IndexedCheckBox removeIcon;
            TextView assessmentName;
            TextView assessmentDate;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                removeIcon = new IndexedCheckBox(context);
                removeIcon.setBackgroundColor(orangeColor);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetails);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                removeIcon = new IndexedCheckBox(context);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }
            //set content
            banner.setId(generateViewId());
            banner.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
            layout.addView(banner);

            removeIcon.setId(generateViewId());
            removeIcon.setPadding(21, 21, 5, 21);
            removeIcon.setViewIndex(viewIndex++);
            removeIcon.setChecked(false);
            layout.addView(removeIcon);

            assessmentName.setId(generateViewId());
            assessmentName.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
            assessmentName.setText(a.getName());
            layout.addView(assessmentName);

            assessmentDate.setId(generateViewId());
            assessmentDate.setText(a.getAssessmentDate());
            assessmentDate.setOnClickListener(new ModifyAssessmentAction(viewIndex++));
            layout.addView(assessmentDate);

            // add constraints
            addBannerConstraints(constraintSet, layout.getId(), banner.getId(), removeIcon.getId(), bannerConnectorId);
            addRemoveIconConstraint(constraintSet, removeIcon.getId(), banner.getId());
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
        for (String note : notes) {
            //TODO add view for title and an image to delete?
            TextView noteView = new TextView(context);
            noteView.setId(generateViewId());
            noteView.setText(note);
            layout.addView(noteView);

            int noteViewId = noteView.getId();
            constraintSet.connect(noteViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            if (bannerConnectorId == layout.getId()) {
                constraintSet.connect(noteViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            } else {
                constraintSet.connect(noteViewId, ConstraintSet.TOP, bannerConnectorId, ConstraintSet.BOTTOM);
            }
            constraintSet.constrainHeight(noteViewId, ConstraintSet.WRAP_CONTENT);
            constraintSet.setMargin(noteViewId, ConstraintSet.START, marginStart);

            bannerConnectorId = noteViewId;
        }
        constraintSet.applyTo(layout);
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

    public void deleteSelectedAssessments(View view) {
        List<Integer> indices = new ArrayList<>();
        ConstraintLayout notesLayout = findViewById(R.id.assessmentContainer);
        for (int i = 0; i < notesLayout.getChildCount(); i++) {
            View child = notesLayout.getChildAt(i);
            if (child instanceof IndexedCheckBox) {
                IndexedCheckBox checkBox = (IndexedCheckBox) child;
                if (checkBox.isChecked()) {
                    indices.add(checkBox.getViewIndex());
                }
            }
        }

        String toastMessage;
        if (indices.size() > 0) {
            StringBuilder warning = new StringBuilder("You are preparing to delete the following: \n");

            toBeDeletedAssessment = toBeDeletedAssessment == null ? new ArrayList<>(indices.size()) : toBeDeletedAssessment;
            List<Assessment> savedAssessments = course == null ? new ArrayList<>() : course.getAssessments();
            for (Integer index : indices) {
                int collectionIndex = index / VIEWS_PER_ROW;
                Assessment a;
                if (savedAssessments.size() > collectionIndex) {
                    a = savedAssessments.get(collectionIndex);
                    //prep for removal in a latter iteration; can't remove now, otherwise indices will be out of sync
                    savedAssessments.set(collectionIndex, null);
                    toBeDeletedAssessment.add(a);
                } else {
                    collectionIndex = course == null ? collectionIndex : collectionIndex - course.getAssessments().size();
                    a = toBeAssessments.get(collectionIndex);
                    //prep for removal in a latter iteration; can't remove now, otherwise indices will be out of sync
                    toBeAssessments.set(collectionIndex, null);
                }
                warning.append("\"").append(a.getName()).append("\", ");
            }
            warning.deleteCharAt(warning.lastIndexOf(","));
            warning.append("\nNOTE: MODIFICATIONS to assessments will NOT be permanent until you SAVE.");
            toastMessage = warning.toString();

            //copy non-nulls over to new array in preparation to replace old
            List<Assessment> newSavedAssessments = new ArrayList<>();
            for (int i = 0; i < savedAssessments.size(); i++) {
                Assessment a = savedAssessments.get(i);
                if (a != null) {
                    newSavedAssessments.add(a);
                }
            }
            if (course != null) {
                course.setAssessments(newSavedAssessments);
            }

            //copy non-nulls over to new array in preparation to replace old
            if (toBeAssessments != null) {
                List<Assessment> newToBeAssessments = new ArrayList<>();
                for (int i = 0; i < toBeAssessments.size(); i++) {
                    Assessment a = toBeAssessments.get(i);
                    if (a != null) {
                        newToBeAssessments.add(a);
                    }
                }
                toBeAssessments = newToBeAssessments;
            }

            clearAssessments();
            insertAssessments(getSessionAssessments());
        } else {
            toastMessage = "Please select a course as a candidate for deletion";
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    public void deleteSelectedNotes(View view) {
        ConstraintLayout notesLayout = findViewById(R.id.courseNotesContainer);

    }

    @Override
    public void cancel(View view) {
        if (course != null) {
            String courseName = getEditTextValue(R.id.courseNameEditText);
            String courseCode = getEditTextValue(R.id.courseCodeEditText);
            String courseStartDate = getEditTextValue(R.id.courseStartDateEditText);
            String courseEndDate = getEditTextValue(R.id.courseEndDateEditText);
            Course modifiedCourse = new Course(course.getId(), courseName, courseCode, courseStartDate, courseEndDate, getSelectedStatus());
            if (!course.equals(modifiedCourse) || toBeAssessments != null || toBeNotes != null) {
                confirmCancel();
            } else {
                finish();
            }
        } else {
            super.cancel(view);
        }
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
                Course modifiedCourse = new Course(courseId, courseName, courseCode, DateTimeUtil.getDateString(courseStartDate), DateTimeUtil.getDateString(courseEndDate), selectedStatus);
                if (!course.equals(modifiedCourse)) {
                    Log.i("UPDATE", "Updating course information from \n" + course + " \n to \n" + modifiedCourse);
                    repositoryManager.updateCourse(courseId, courseName, courseCode, courseStartDate, courseEndDate, selectedStatus.getStatus());
                }

                List<Assessment> modifiedAssessments = new ArrayList<>();
                List<Assessment> currentAssessments = course.getAssessments();
                for (int i = 0; i < currentAssessments.size(); i++) {
                    Assessment cAssessment = currentAssessments.get(i);
                    Assessment oAssessment = originalAssessments.get(i);
                    if (cAssessment.getId().equals(oAssessment.getId())) {
                        if (!cAssessment.equals(oAssessment)) {
                            modifiedAssessments.add(cAssessment);
                        }
                    } else {
                        Log.e("INDEXING_ERROR", "IDs should be the same for saved assessments with same indexes: " +
                                "\n Original Assessment Details" + oAssessment + "\n Current Assessment Details " + cAssessment);
                    }
                }
                if (modifiedAssessments.size() > 0) {
                    Log.d("UPDATE", "updating assessment details for modified assessments.");
                    int[] ids = repositoryManager.updateAssessments(modifiedAssessments);
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
        String termStartDateString = term.getStartDate();
        String termEndDateString = term.getEndDate();
        long termStartDate = DateTimeUtil.getSecondsSinceEpoch(termStartDateString);
        long termEndDate = DateTimeUtil.getSecondsSinceEpoch(termEndDateString);
        if (termStartDate > courseStartDate || termEndDate < courseStartDate) {
            invalidValues.add(R.id.courseStartDateEditText);

            String error = "The course start date must be within the planned term dates " + termStartDateString + " - " + termEndDateString + ".";
            errorMessage = errorMessage == null ? error : errorMessage + "\n" + error;
        }

        if (termStartDate > courseEndDate || termEndDate < courseEndDate) {
            invalidValues.add(R.id.courseStartDateEditText);
            String error = "The course end date must be within the planned term dates " + termStartDateString + " - " + termEndDateString + ".";
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
            if (requestCode == GET_ASSESSMENT_RESULT) {
                Assessment newAssessment = (Assessment) data.getExtras().getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
                if (newAssessment != null) {
                    if (toBeAssessments == null) {
                        toBeAssessments = new ArrayList<>();
                    }
                    toBeAssessments.add(newAssessment);
                    insertAssessments(getSessionAssessments());
                }
            } else if (requestCode == GET_NOTE_RESULT) {
                String newNote = data.getExtras().getString(COURSE_NOTE_BUNDLE_KEY);
                if (newNote != null) {
                    if (toBeNotes == null) {
                        toBeNotes = new ArrayList<>();
                    }
                    toBeNotes.add(newNote);
                }
                if (course != null) {
                    course.getCourseNotes().add(newNote);
                    insertNotes(course.getCourseNotes());
                } else {
                    insertNotes(toBeNotes);
                }
            } else if (requestCode == MODIFY_ASSESSMENT_RESULT) {
                if (course != null) {
                    Bundle extras = data.getExtras();
                    if (extras.getBoolean(IS_MODIFIED)) {
                        int collectionIndex = extras.getInt(ARRAY_INDEX_KEY);
                        //get target collection reference
                        List<Assessment> assessments = extras.getBoolean(IS_NEW_ITEM) ? toBeAssessments : course.getAssessments();
                        //get modified assessment
                        Assessment modifiedAssessment = (Assessment) extras.getSerializable(ASSESSMENT_OBJECT_BUNDLE_KEY);
                        //modify collection by replacing old value with new modified value
                        assessments.remove(collectionIndex);
                        assessments.add(collectionIndex, modifiedAssessment);
                    }
                    insertAssessments(getSessionAssessments());
                }
            }
        }
    }


    private void insertAllAvailableAssessments() {
        if (course != null) {
            insertAssessments(CollectionUtil.copyAndAdd(course.getAssessments(), toBeAssessments));
        } else {
            //otherwise, it's a new course which hasn't been saved yet
            insertAssessments(toBeAssessments);
        }
    }

    private List<Assessment> getSessionAssessments() {
        List<Assessment> sessionAssessments;
        if (course == null && toBeAssessments == null) {
            sessionAssessments = new ArrayList<>();
        } else if (course == null) {
            sessionAssessments = toBeAssessments;
        } else if (toBeAssessments == null) {
            sessionAssessments = course.getAssessments();
        } else {
            sessionAssessments = CollectionUtil.copyAndAdd(course.getAssessments(), toBeAssessments);
        }
        return sessionAssessments;
    }

    public void createNoteAction(View view) {
        startActivityForResult(new Intent(getApplicationContext(), CourseNoteActivity.class), GET_NOTE_RESULT);
    }


    //////ADD BELOW BACK TO PARENT CLASS WHEN DONE WITH POC HERE  //////////
    void addRemoveIconConstraint(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
    }

    void addBannerConstraints(ConstraintSet constraintSet, int containerId, int constrainedViewId, int removeIconId, int connectorId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, removeIconId, ConstraintSet.END);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        if (connectorId == containerId) {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.TOP);
        } else {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.BOTTOM);
        }
        //height is not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, bannerHeight);
    }

    void addPlanNamesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, bannerId, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
//        constraintSet.setMargin(constrainedViewId, ConstraintSet.START, marginStart);
    }

    void addModifiedDatesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
        constraintSet.setMargin(constrainedViewId, ConstraintSet.END, marginEnd);
    }
    //////ADD ABOVE BACK TO PARENT CLASS WHEN DONE WITH POC HERE  //////////


    //TODO add similar functionality for notes....
    private class ModifyAssessmentAction implements View.OnClickListener {

        ModifyAssessmentAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private final int viewIndex;

        @Override
        public void onClick(View v) {
//            if (requestRemoval) {
//                Assessment assessment;
//                int collectionIndex = this.viewIndex / VIEWS_PER_ROW;
//                boolean isNewItem = false;
//                if (course != null && course.getAssessments().size() > collectionIndex) {
//                    assessment = course.getAssessments().get(collectionIndex);
//                } else {
//                    isNewItem = true;
//                    //adjust collection index as the view initially treats pre-saved and toBe assessments as one whole collection.
//                    collectionIndex = course == null ? collectionIndex : collectionIndex - course.getAssessments().size();
//                    assessment = toBeAssessments.get(collectionIndex);
//                }
//
//                //TODO add confirmation dialog here ('You sure you wanna delete this thing?')
//
//                if (isNewItem) {
//                    toBeAssessments.remove(collectionIndex);
//                } else {
//                    repositoryManager.deleteAssessment(assessment);
//                    course.getAssessments().remove(collectionIndex);
//                }
//
//                insertAssessments(getSessionAssessments()); //TODO... this won't re-draw... this will just add, no?
//
//            }


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
                //capture assessment to modify and some meta data for tracking between activities
                Assessment assessment;
                int collectionIndex = this.viewIndex / VIEWS_PER_ROW;
                boolean isNewItem = false;
                if (course != null && course.getAssessments().size() > collectionIndex) {
                    assessment = course.getAssessments().get(collectionIndex);
                } else {
                    isNewItem = true;
                    //adjust collection index as the view initially treats pre-saved and toBe assessments as one whole collection.
                    collectionIndex = course == null ? collectionIndex : collectionIndex - course.getAssessments().size();
                    assessment = toBeAssessments.get(collectionIndex);
                }

                Intent assessmentDetailsActivity = new Intent(getApplicationContext(), AssessmentDetailsActivity.class);
                assessmentDetailsActivity.putExtra(COURSE_START_DATE_BUNDLE_KEY, courseStartDate);
                assessmentDetailsActivity.putExtra(COURSE_END_DATE_BUNDLE_KEY, courseEndDate);
                assessmentDetailsActivity.putExtra(ASSESSMENT_OBJECT_BUNDLE_KEY, assessment);
                assessmentDetailsActivity.putExtra(ARRAY_INDEX_KEY, collectionIndex);
                assessmentDetailsActivity.putExtra(IS_NEW_ITEM, isNewItem);
                startActivityForResult(assessmentDetailsActivity, MODIFY_ASSESSMENT_RESULT);
            }
        }
    }

}
