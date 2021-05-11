package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.term.Term;
import edu.wgu.android.studentscheduler.fragment.ConfirmationDialogFragment;
import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;
import edu.wgu.android.studentscheduler.persistence.DegreePlanRepositoryManager;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static edu.wgu.android.studentscheduler.util.StringUtil.isEmpty;

public class StudentSchedulerActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationDialogListener {

    public static final String DEGREE_PLAN_ID_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.degreePlanId";
    public static final String DEGREE_PLAN_NAME_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.degreePlanName";
    public static final String DEGREE_PLAN_STUDENT_NAME_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.degreePlanStudentName";
    public static final String IS_FIRST_LOAD_KEY = "edu.wgu.android.studentscheduler.activity.isFirstLoad";
    public static final String IS_MODIFIED = "edu.wgu.android.studentscheduler.activity.isModified";
    public static final String ARRAY_INDEX_KEY = "edu.wgu.android.studentscheduler.activity.arrayIndexKey";
    public static final String IS_NEW_ITEM = "edu.wgu.android.studentscheduler.activity.isNewItem";
    public static final String TERM_ID_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.termId";
    public static final String TERM_OBJECT_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.termObject";
    public static final String COURSE_ID_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.courseObject";
    public static final String COURSE_START_DATE_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.courseStartDate";
    public static final String COURSE_END_DATE_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.courseEndDate";
    public static final String COURSE_OBJECT_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.courseObject";
    public static final String COURSE_NOTE_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.courseNote";
    public static final String ASSESSMENT_OBJECT_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.assessmentObject";

    static final int VIEWS_PER_ROW = 4;

    //TODO shouldn't be calling a repo from a view layer;  move this to a business layer and invoke that instead
    final DegreePlanRepositoryManager repositoryManager = DegreePlanRepositoryManager.getInstance(this);

    //Have to be injected via constraints for dynamic ConstraintLayout as they are not honored from TextView styles
    int bannerHeight;
    int marginStart;
    int marginEnd;
    int checkboxHeight;

    int invalidEntryColor;
    int validEntryColor;
    int orangeColor;
    int whiteColor;

    Drawable errorBorder;


    StudentSchedulerActivity(@LayoutRes int id) {
        super(id);
    }

    /**
     * To initialize standard colors and other key values used to dynamically modify
     * styles or other views. Should only be called after onCreate (or at least after
     * the super.onCreate() method is invoked).
     */
    void init() {
        Resources resources = getResources();
        bannerHeight = resources.getDimensionPixelSize(R.dimen.text_view_list_layout_height);
        marginStart = resources.getDimensionPixelSize(R.dimen.text_view_list_layout_marginStart);
        marginEnd = resources.getDimensionPixelSize(R.dimen.text_view_list_layout_marginEnd);
        checkboxHeight = resources.getDimensionPixelSize(R.dimen.check_box_height);

        invalidEntryColor = resources.getColor(R.color.red_orange);
        validEntryColor = resources.getColor(R.color.white);
        orangeColor = resources.getColor(R.color.orange);
        whiteColor = resources.getColor(R.color.white);

        errorBorder = ResourcesCompat.getDrawable(resources, R.drawable.error_border, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    /**
     * @param view - the edit text view which calls the date picker fragment
     */
    public void showDatePickerDialog(View view) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DatePickerFragment datePicker = new DatePickerFragment((EditText) view);
        datePicker.show(supportFragmentManager, "datePicker");
    }

    public void cancel(View view) {
        boolean viewModified = isViewModified(view);

        if (viewModified) {
            confirmCancel();
        } else {
            finish();  //TODO causes issues is someone bonks cancel twice in the same activity (because this one closes)
        }
    }

    void confirmCancel() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ConfirmationDialogFragment confirmation = new ConfirmationDialogFragment();
        confirmation.show(supportFragmentManager, "cancelConfirmation");
    }

    boolean isViewModified(View view) {
        boolean viewModified = false;
        int i = 0;
        ViewGroup container = (ViewGroup) view.getParent();
        int childCount = container.getChildCount();
        do {
            View child = container.getChildAt(i++);
            if (child instanceof EditText) {
                viewModified = !isEmpty(((EditText) child).getText().toString());
            } else if (child instanceof RadioGroup) {
                int j = 0;
                RadioGroup radioGroup = (RadioGroup) child;
                int rButtonCount = radioGroup.getChildCount();
                do {
                    RadioButton rButton = (RadioButton) radioGroup.getChildAt(j++);
                    // the default button will be checked and also have a hint of 'default'; no other buttons will have a hint
                    viewModified = rButton.isChecked() && rButton.getHint() == null;
                } while (j < rButtonCount && !viewModified);
            }
        } while (i < childCount && !viewModified);
        return viewModified;
    }

    String getRequiredTextValue(@IdRes int editTextField, Set<Integer> invalidValues) {
        return getRequiredTextValue(editTextField, invalidValues, null);
    }

    String getRequiredTextValue(@IdRes int editTextField, Set<Integer> invalidValues, Set<Integer> validValues) {
        String textValue = getEditTextValue(editTextField);
        if (isEmpty(textValue)) {
            invalidValues.add(editTextField);
        } else if(validValues != null) {
            validValues.add(editTextField);
        }

        return textValue;
    }

    long getRequiredDate(@IdRes int dateEditTextId, Set<Integer> invalidValues) {
        String startDate = getEditTextValue(dateEditTextId);
        long startDateSeconds = 0;
        if (isEmpty(startDate)) {
            invalidValues.add(dateEditTextId);
        } else {
            startDateSeconds = DateTimeUtil.getSecondsSinceEpoch(startDate);
            if (startDateSeconds == 0) {
                invalidValues.add(dateEditTextId);
            }
        }
        return startDateSeconds;
    }

    String getEditTextValue(@IdRes int id) {
        String value = null;
        View view = findViewById(id);
        if (view != null) {
            value = ((EditText) view).getText().toString();
        }
        return value;
    }

    String getRadioGroupSelection(@IdRes int id) {
        String selection = null;
        View view = findViewById(id);
        if (view instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) view;
            int rButtonCount = radioGroup.getChildCount();
            for (int i = 0; i < rButtonCount; i++) {
                RadioButton rButton = (RadioButton) radioGroup.getChildAt(i);
                if (rButton.isChecked()) {
                    selection = rButton.getText().toString();
                }
            }
        }
        return selection;
    }

    // used for the 3 per view setup
    void addBannerConstraints(ConstraintSet constraintSet, int containerId, int constrainedViewId, int connectorId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        if (connectorId == containerId) {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.TOP);
        } else {
            constraintSet.connect(constrainedViewId, ConstraintSet.TOP, connectorId, ConstraintSet.BOTTOM);
        }
        //height is not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, bannerHeight);
    }

    //used for the 4 per view setup
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

    void addRemoveIconConstraint(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, checkboxHeight);
    }

    void addPlanNamesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.START, bannerId, ConstraintSet.START);
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
    }

    void addModifiedDatesConstraints(ConstraintSet constraintSet, int constrainedViewId, int bannerId) {
        constraintSet.connect(constrainedViewId, ConstraintSet.TOP, bannerId, ConstraintSet.TOP);
        constraintSet.connect(constrainedViewId, ConstraintSet.BOTTOM, bannerId, ConstraintSet.BOTTOM);
        constraintSet.connect(constrainedViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        //height and margins are not honored from styles in dynamic ConstraintLayouts
        constraintSet.constrainHeight(constrainedViewId, ConstraintSet.WRAP_CONTENT);
        constraintSet.setMargin(constrainedViewId, ConstraintSet.END, marginEnd);
    }

    @Override
    public void onPositive(int key) {
        finish(); // close the activity
    }

    public void showCourseDetailsActivity(Term term, long courseId) {
        Intent courseDetailsActivity = new Intent(getApplicationContext(), CourseDetailsActivity.class);
        courseDetailsActivity.putExtra(TERM_OBJECT_BUNDLE_KEY, term);
        courseDetailsActivity.putExtra(COURSE_ID_BUNDLE_KEY, courseId);
        startActivity(courseDetailsActivity);
    }
}
