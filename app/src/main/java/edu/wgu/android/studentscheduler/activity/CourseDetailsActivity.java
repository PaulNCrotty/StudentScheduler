package edu.wgu.android.studentscheduler.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import edu.wgu.android.studentscheduler.alert.AlertRequester;
import edu.wgu.android.studentscheduler.domain.CourseNote;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseInstructor;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;
import edu.wgu.android.studentscheduler.persistence.contract.DegreePlanContract;
import edu.wgu.android.studentscheduler.util.CollectionUtil;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;
import edu.wgu.android.studentscheduler.widget.IndexedCheckBox;

import static android.view.View.generateViewId;


/***
 * Bugs and missing features:
 *   isn't deleting previously saved assessments or notes... only new or 'toBe' versions are getting deleted
 *
 */
public class CourseDetailsActivity extends StudentSchedulerActivity {

    private static final int GET_ASSESSMENT_RESULT = 0;
    private static final int GET_NOTE_RESULT = 1;
    private static final int MODIFY_ASSESSMENT_RESULT = 2;
    private static final int MODIFY_NOTE_RESULT = 3;
    private static final int DELETE_ASSESSMENT_KEY = 4;
    private static final String ORIGINAL_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.originalAssessments";
    private static final String ORIGINAL_COURSE_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.originalNotes";
    private static final String TO_BE_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeAssessments";
    private static final String TO_BE_DELETED_ASSESSMENTS_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeDeletedAssessments";
    private static final String TO_BE_DELETED_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeDeletedNotes";
    private static final String TO_BE_COURSE_NOTES_ARRAY_KEY = "edu.wgu.android.studentscheduler.activity.toBeCourseNotes";

    private static final int COURSE_START_DATE_NOTIFICATION_KEY = 0;
    private static final int COURSE_END_DATE_NOTIFICATION_KEY = 1;
    private static final int ASSESSMENT_ATTEMPT_DATE_NOTIFICATION_KEY = 2;

    public CourseDetailsActivity() {
        super(R.layout.activity_course_detail);
    }

    private Term term;
    private Course course;
    private List<Assessment> originalAssessments;   //used to track and compare if assessments have changed
    private List<Assessment> toBeAssessments;       //transient assessments; must be saved at end of activity
    private List<Assessment> toBeDeletedAssessment; //transient assessments to be deleted; must be saved at end of activity for deletion to be permanent
    private List<CourseNote> originalNotes;         //used to track and compare if notes have changed
    private List<CourseNote> toBeDeletedNotes;      //transient notes to be deleted; must be saved at end of activity for deletion to be permanent
    private List<CourseNote> toBeNotes;             //transient assessments; must be saved at end of activity

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
        if (toBeDeletedNotes != null) {
            savedInstanceState.putSerializable(TO_BE_DELETED_NOTES_ARRAY_KEY, (Serializable) toBeDeletedNotes);
        }
        if (originalNotes != null) {
            savedInstanceState.putSerializable(ORIGINAL_COURSE_NOTES_ARRAY_KEY, (Serializable) originalNotes);
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
                toBeNotes = (ArrayList<CourseNote>) notes;
            }

            Serializable oAssessments = savedInstanceState.getSerializable(ORIGINAL_ASSESSMENTS_ARRAY_KEY);
            if (oAssessments instanceof ArrayList) {
                originalAssessments = (ArrayList<Assessment>) oAssessments;
            }

            Serializable dAssessments = savedInstanceState.getSerializable(TO_BE_DELETED_ASSESSMENTS_ARRAY_KEY);
            if (dAssessments instanceof ArrayList) {
                toBeDeletedAssessment = (ArrayList<Assessment>) dAssessments;
            }

            Serializable dNotes = savedInstanceState.getSerializable(TO_BE_DELETED_NOTES_ARRAY_KEY);
            if (dNotes instanceof ArrayList) {
                toBeDeletedNotes = (ArrayList<CourseNote>) dAssessments;
            }

            Serializable oNotes = savedInstanceState.getSerializable(ORIGINAL_COURSE_NOTES_ARRAY_KEY);
            if(oNotes instanceof ArrayList) {
                originalNotes = (ArrayList<CourseNote>) oNotes;
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
                List<CourseNote> notes = repositoryManager.getCourseNotes(courseId);
                // deep copy ... otherwise, we'd just have an alias
                originalNotes = new ArrayList<>(notes);
                course.getCourseNotes().addAll(notes);
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
        }

        // Dynamically add assessments and notes to layout if they exist
        // Note: copyAndAdd returns a deep copy of the collections
        List<Assessment> assessmentsToDisplay = getSessionAssessments();
        if (assessmentsToDisplay.size() > 0) {
            insertAssessments(assessmentsToDisplay);
        }

        List<CourseNote> notesToDisplay = getSessionNotes();
        if (notesToDisplay.size() > 0) {
            insertNotes(notesToDisplay);
        }
    }

    private void clearAssessments() {
        ConstraintLayout layout = findViewById(R.id.assessmentContainer);
        layout.removeAllViews();
    }

    private void clearNotes() {
        ConstraintLayout layout = findViewById(R.id.courseNotesContainer);
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
            IndexedCheckBox removeIcon = new IndexedCheckBox(context);
            TextView assessmentName;
            TextView assessmentDate;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                removeIcon.setBackgroundColor(orangeColor);
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

            removeIcon.setId(generateViewId());
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

    private void insertNotes(List<CourseNote> notes) {
        ConstraintLayout layout = findViewById(R.id.courseNotesContainer);
        Context context = layout.getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        int viewIndex = 0;
        int bannerConnectorId = layout.getId();
        boolean useStandardStyles = true;
        for (CourseNote note : notes) {
            //declare views and prep for basic color styles
            TextView banner;
            IndexedCheckBox removeIcon = new IndexedCheckBox(context);
            TextView assessmentName;
            TextView assessmentDate;
            if (useStandardStyles) {
                banner = new TextView(context, null, 0, R.style.listOptionBanner);
                removeIcon.setBackgroundColor(orangeColor);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetails);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDates);
            } else {
                banner = new TextView(context, null, 0, R.style.listOptionBannerAlt);
                assessmentName = new TextView(context, null, 0, R.style.listOptionDetailsAlt);
                assessmentDate = new TextView(context, null, 0, R.style.listOptionDatesAlt);
            }
            //set content
            banner.setId(generateViewId());
            banner.setOnClickListener(new ModifyNoteAction(viewIndex++));
            layout.addView(banner);

            removeIcon.setId(generateViewId());
            removeIcon.setViewIndex(viewIndex++);
            removeIcon.setChecked(false);
            layout.addView(removeIcon);

            assessmentName.setId(generateViewId());
            assessmentName.setOnClickListener(new ModifyNoteAction(viewIndex++));
            assessmentName.setText(note.getTitle());
            layout.addView(assessmentName);

            String date = note.getModifiedDate() == null ? note.getCreatedDate() : note.getModifiedDate();
            assessmentDate.setId(generateViewId());
            assessmentDate.setText(date);
            assessmentDate.setOnClickListener(new ModifyNoteAction(viewIndex++));
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
            StringBuilder warning = new StringBuilder("You are preparing to delete the following assessments: \n");

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
            toastMessage = "Please select at least one course as a candidate for deletion";
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    public void deleteSelectedNotes(View view) {
        List<Integer> indices = new ArrayList<>();
        ConstraintLayout layout = findViewById(R.id.courseNotesContainer);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof IndexedCheckBox) {
                IndexedCheckBox checkBox = (IndexedCheckBox) child;
                if (checkBox.isChecked()) {
                    indices.add(checkBox.getViewIndex());
                }
            }
        }

        String toastMessage;
        if (indices.size() > 0) {
            StringBuilder warning = new StringBuilder("You are preparing to delete the following course notes: \n");

            toBeDeletedNotes = toBeDeletedNotes == null ? new ArrayList<>(indices.size()) : toBeDeletedNotes;
            List<CourseNote> savedNotes = course == null ? new ArrayList<>() : course.getCourseNotes();
            for (Integer index : indices) {
                int collectionIndex = index / VIEWS_PER_ROW;
                CourseNote a;
                if (savedNotes.size() > collectionIndex) {
                    a = savedNotes.get(collectionIndex);
                    //prep for removal in a latter iteration; can't remove now, otherwise indices will be out of sync
                    savedNotes.set(collectionIndex, null);
                    toBeDeletedNotes.add(a);
                } else {
                    collectionIndex = course == null ? collectionIndex : collectionIndex - course.getCourseNotes().size();
                    a = toBeNotes.get(collectionIndex);
                    //prep for removal in a latter iteration; can't remove now, otherwise indices will be out of sync
                    toBeNotes.set(collectionIndex, null);
                }
                warning.append("\"").append(a.getTitle()).append("\", ");
            }
            warning.deleteCharAt(warning.lastIndexOf(","));
            warning.append("\nNOTE: MODIFICATIONS to notes will NOT be permanent until you SAVE.");
            toastMessage = warning.toString();

            //copy non-nulls over to new array in preparation to replace old
            List<CourseNote> newSavedNotes = new ArrayList<>();
            for (int i = 0; i < savedNotes.size(); i++) {
                CourseNote a = savedNotes.get(i);
                if (a != null) {
                    newSavedNotes.add(a);
                }
            }
            if (course != null) {
                course.setCourseNotes(newSavedNotes);
            }

            //copy non-nulls over to new array in preparation to replace old
            if (toBeNotes != null) {
                List<CourseNote> newToBeNotes = new ArrayList<>();
                for (int i = 0; i < toBeNotes.size(); i++) {
                    CourseNote a = toBeNotes.get(i);
                    if (a != null) {
                        newToBeNotes.add(a);
                    }
                }
                toBeNotes = newToBeNotes;
            }

            clearNotes();
            insertNotes(getSessionNotes());
        } else {
            toastMessage = "Please select at least one course as a candidate for deletion";
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
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
                managerAlerts(new Course(courseId, courseName, courseCode, DateTimeUtil.getDateString(courseStartDate), DateTimeUtil.getDateString(courseEndDate), selectedStatus));
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

                //check for and persiste (update) any modifications to existing assessments
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

                //check for and persist (update) any modifications to existing notes
                List<CourseNote> modifiedNotes = new ArrayList<>();
                List<CourseNote> currentNotes = course.getCourseNotes();
                for(int i = 0; i < currentNotes.size(); i++) {
                    CourseNote cCourseNote = currentNotes.get(i);
                    CourseNote oCourseNote = originalNotes.get(i);
                    if(cCourseNote.getId() == oCourseNote.getId()) {
                        if(!cCourseNote.equals(oCourseNote)) {
                            modifiedNotes.add(cCourseNote);
                        }
                    }
                }
                if(modifiedNotes.size() > 0) {
                    Log.d("UPDATE", "updating course details for modified notes.");
                    int[] ids = repositoryManager.updateCourseNotes(courseId, modifiedNotes);
                }

            }
            if (toBeAssessments != null) {
                long[] ids = repositoryManager.insertAssessments(courseId, toBeAssessments); //TODO what about modifications/upates to courses?
            }
            if (toBeNotes != null) {
                long[] ids = repositoryManager.insertCourseNotes(courseId, toBeNotes); //TODO what about modifications/updates to course notes?
            }
            if (toBeDeletedAssessment != null && toBeDeletedAssessment.size() > 0) {
                List<Long> idsToDelete = new ArrayList<>(toBeDeletedAssessment.size());
                for (Assessment a : toBeDeletedAssessment) {
                    idsToDelete.add(a.getId());
                }
                repositoryManager.deleteEntries(idsToDelete, DegreePlanContract.Assessment.TABLE_NAME);
            }
            if (toBeDeletedNotes != null && toBeDeletedNotes.size() > 0) {
                List<Long> idsToDelete = new ArrayList<>(toBeDeletedNotes.size());
                for (CourseNote a : toBeDeletedNotes) {
                    idsToDelete.add(a.getId());
                }
                repositoryManager.deleteEntries(idsToDelete, DegreePlanContract.CourseNote.TABLE_NAME);
            }
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
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

    private void managerAlerts(Course course) {
        Notification.Builder builder;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, AlertRequester.NOTIFICATION_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
                .setContentTitle("New Course Starting")
                .setContentText("You're course " + course.getCourseName() + " is about to start")
                .setSmallIcon(R.drawable.edit_calendar_icon)
                .build();

        Intent alertRequest = new Intent(this, AlertRequester.class);
        alertRequest.putExtra(AlertRequester.NOTIFICATION_ID, COURSE_START_DATE_NOTIFICATION_KEY);
        alertRequest.putExtra(AlertRequester.NOTIFICATION_KEY, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, COURSE_START_DATE_NOTIFICATION_KEY, alertRequest, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000, pendingIntent);
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
                CourseNote newNote = (CourseNote) data.getExtras().getSerializable(COURSE_NOTE_BUNDLE_KEY);
                if (newNote != null) {
                    if (toBeNotes == null) {
                        toBeNotes = new ArrayList<>();
                    }
                    toBeNotes.add(newNote);
                    insertNotes(getSessionNotes());
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
            } else if (requestCode == MODIFY_NOTE_RESULT) {
                Bundle extras = data.getExtras();
                if (extras.getBoolean(IS_MODIFIED)) {
                    int collectionIndex = extras.getInt(ARRAY_INDEX_KEY);
                    List<CourseNote> notes = extras.getBoolean(IS_NEW_ITEM) ? toBeNotes : course.getCourseNotes();
                    CourseNote modifiedNote = (CourseNote) extras.getSerializable(COURSE_NOTE_BUNDLE_KEY);
                    notes.remove(collectionIndex);
                    notes.add(collectionIndex, modifiedNote);
                }
                insertNotes(getSessionNotes());
            }
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

    private List<CourseNote> getSessionNotes() {
        List<CourseNote> sessionNotes;
        if (course == null && toBeNotes == null) {
            sessionNotes = new ArrayList<>();
        } else if (course == null) {
            sessionNotes = toBeNotes;
        } else if (toBeNotes == null) {
            sessionNotes = course.getCourseNotes();
        } else {
            sessionNotes = CollectionUtil.copyAndAdd(course.getCourseNotes(), toBeNotes);
        }
        return sessionNotes;
    }

    public void createNoteAction(View view) {
        startActivityForResult(new Intent(getApplicationContext(), CourseNoteActivity.class), GET_NOTE_RESULT);
    }


    //TODO add similar functionality for notes....
    private class ModifyAssessmentAction implements View.OnClickListener {

        ModifyAssessmentAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        private final int viewIndex;

        @Override
        public void onClick(View v) {
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

    private class ModifyNoteAction implements View.OnClickListener {

        int viewIndex;

        ModifyNoteAction(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        @Override
        public void onClick(View v) {
            CourseNote note;

            boolean isNewItem = false;
            int index = this.viewIndex / VIEWS_PER_ROW;
            if (course != null && course.getCourseNotes().size() > index) {
                note = course.getCourseNotes().get(index);
            } else {
                isNewItem = true;
                //adjust index to accommodate any previously saved course notes (if exists)
                index = course == null ? index : index - course.getCourseNotes().size();
                note = toBeNotes.get(index);
            }

            Intent courseNoteActivity = new Intent(getApplicationContext(), CourseNoteActivity.class);
            courseNoteActivity.putExtra(COURSE_NOTE_BUNDLE_KEY, note);
            courseNoteActivity.putExtra(ARRAY_INDEX_KEY, index);
            courseNoteActivity.putExtra(IS_NEW_ITEM, isNewItem);
            startActivityForResult(courseNoteActivity, MODIFY_NOTE_RESULT);
        }

    }

}
