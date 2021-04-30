package edu.wgu.android.studentscheduler.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import edu.wgu.android.studentscheduler.R;

import static android.view.View.generateViewId;

public class DegreePlanFragmentPoc extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        ConstraintLayout degreePlanContainer = (ConstraintLayout) inflater.inflate(R.layout.activity_degree_plan, container, false);
        Log.d("IDS", String.format("Degree Plan Container ID: %d",degreePlanContainer.getId())); //2131230845

        Context fragmentContext = degreePlanContainer.getContext();
        int defaultTextHeight = 100; //getResources().getDimensionPixelSize(R.dimen.text_view_term_list_layout_height);
        int termBannerBackgroundColor = Color.parseColor("#3700B3");
        int courseContainerBackgroundColor = Color.parseColor("#DEDEDE");
        int defaultTextColor = Color.parseColor("#FFFFFF");
        int courseTextColor = Color.parseColor("#3700B3");

        // Degree plan level constraints (highest container level)
        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(degreePlanContainer);

        //High-level term one details
        TextView termOneBackground = new TextView(fragmentContext);
        termOneBackground.setId(generateViewId());
//        termOneBackground.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, defaultTextHeight)); //get overwritten by constraint set... of no use
        termOneBackground.setBackgroundColor(termBannerBackgroundColor);
        degreePlanContainer.addView(termOneBackground);

        TextView termName = new TextView(fragmentContext);
        termName.setId(generateViewId());
//        termName.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, defaultTextHeight)); //get overwritten by constraint set... of no use
        termName.setBackgroundColor(termBannerBackgroundColor);
        termName.setTextColor(defaultTextColor);
        termName.setText("Term One");
        degreePlanContainer.addView(termName);

        TextView termDates = new TextView(fragmentContext);
        termDates.setId(generateViewId());
        termDates.setBackgroundColor(termBannerBackgroundColor);
        termDates.setTextColor(defaultTextColor);
        termDates.setText("2021-01-01 until 2021-06-30");
        degreePlanContainer.addView(termDates);

        ImageButton editIcon = new ImageButton(fragmentContext);
        editIcon.setId(generateViewId());
        editIcon.setBackgroundColor(termBannerBackgroundColor);
        editIcon.setImageResource(R.drawable.edit_icon_white);
        degreePlanContainer.addView(editIcon);

        constraints.connect(termOneBackground.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraints.connect(termOneBackground.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraints.connect(termOneBackground.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraints.constrainHeight(termOneBackground.getId(), defaultTextHeight);

        constraints.connect(termName.getId(), ConstraintSet.START, termOneBackground.getId(), ConstraintSet.START);
        constraints.connect(termName.getId(), ConstraintSet.TOP, termOneBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(termName.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(termName.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
        constraints.setMargin(termName.getId(), ConstraintSet.START, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp

        constraints.connect(termDates.getId(), ConstraintSet.END, editIcon.getId(), ConstraintSet.START);
        constraints.connect(termDates.getId(), ConstraintSet.TOP, termOneBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(termDates.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(termDates.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
        constraints.setMargin(termDates.getId(), ConstraintSet.START, 20);
        constraints.setMargin(termDates.getId(), ConstraintSet.END, 20);

        constraints.connect(editIcon.getId(), ConstraintSet.END, termOneBackground.getId(), ConstraintSet.END);
        constraints.connect(editIcon.getId(), ConstraintSet.TOP, termOneBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(editIcon.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(editIcon.getId(), ConstraintSet.END, 20);

        // Course container and high-level course details
        ConstraintLayout termOneCoursesContainer = new ConstraintLayout(fragmentContext);
        termOneCoursesContainer.setId(generateViewId());
        termOneCoursesContainer.setBackgroundColor(courseContainerBackgroundColor);
        degreePlanContainer.addView(termOneCoursesContainer);

        constraints.connect(termOneCoursesContainer.getId(), ConstraintSet.START, termOneBackground.getId(), ConstraintSet.START);
        constraints.connect(termOneCoursesContainer.getId(), ConstraintSet.END, termOneBackground.getId(), ConstraintSet.END);
        constraints.connect(termOneCoursesContainer.getId(), ConstraintSet.TOP, termOneBackground.getId(), ConstraintSet.BOTTOM); //begin where termOneBackground border ends (vertically)
        constraints.constrainHeight(termOneCoursesContainer.getId(), ConstraintSet.WRAP_CONTENT); //TODO default to some generic value to allow for adding a course when no courses exist (newly created term)?

        ConstraintSet termCoursesConstraints = new ConstraintSet();
        termCoursesConstraints.clone(termOneCoursesContainer);

        Context termOneContext = termOneCoursesContainer.getContext();
        ImageView courseOneStatus = new ImageView(termOneContext);
        courseOneStatus.setId(generateViewId());
        courseOneStatus.setImageResource(R.drawable.course_status_passed_15);
        termOneCoursesContainer.addView(courseOneStatus);

        Button courseOneName = new Button(termOneContext);
        courseOneName.setId(generateViewId());
        courseOneName.setTextColor(courseTextColor);
        courseOneName.setText("C191 - Android is hard ... isn't it");
        termOneCoursesContainer.addView(courseOneName);

        TextView courseOneFinishDate = new TextView(termOneContext);
        courseOneFinishDate.setId(generateViewId());
        courseOneFinishDate.setTextColor(courseTextColor);
        courseOneFinishDate.setText("2021-04-15");
        termOneCoursesContainer.addView(courseOneFinishDate);

        termCoursesConstraints.connect(courseOneStatus.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        termCoursesConstraints.connect(courseOneStatus.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        termCoursesConstraints.constrainHeight(courseOneStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseOneStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseOneStatus.getId(), ConstraintSet.TOP, 28);
        termCoursesConstraints.setMargin(courseOneStatus.getId(), ConstraintSet.START, 40);

        termCoursesConstraints.connect(courseOneName.getId(), ConstraintSet.START, courseOneStatus.getId(), ConstraintSet.END);
        termCoursesConstraints.connect(courseOneName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        termCoursesConstraints.constrainHeight(courseOneName.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseOneName.getId(), ConstraintSet.WRAP_CONTENT);
//        termCoursesConstraints.setMargin(courseOneName.getId(), ConstraintSet.TOP, 20);
        termCoursesConstraints.setMargin(courseOneName.getId(), ConstraintSet.START, 40);

        termCoursesConstraints.connect(courseOneFinishDate.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        termCoursesConstraints.connect(courseOneFinishDate.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        termCoursesConstraints.constrainHeight(courseOneFinishDate.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseOneFinishDate.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseOneFinishDate.getId(), ConstraintSet.TOP, 35);
        termCoursesConstraints.setMargin(courseOneFinishDate.getId(), ConstraintSet.END, 30);

        ImageView courseTwoStatus = new ImageView(termOneContext);
        courseTwoStatus.setId(generateViewId());
        courseTwoStatus.setImageResource(R.drawable.course_status_in_progress_15);
        termOneCoursesContainer.addView(courseTwoStatus);

        Button courseTwoName = new Button(termOneContext);
        courseTwoName.setId(generateViewId());
        courseTwoName.setTextColor(courseTextColor);
        courseTwoName.setText("C291 - More Android is even harder ... Wow!");
        termOneCoursesContainer.addView(courseTwoName);

        termCoursesConstraints.connect(courseTwoStatus.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        termCoursesConstraints.connect(courseTwoStatus.getId(), ConstraintSet.TOP, courseOneName.getId(), ConstraintSet.BOTTOM);
        termCoursesConstraints.constrainHeight(courseTwoStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseTwoStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseTwoStatus.getId(), ConstraintSet.TOP, 43);
        termCoursesConstraints.setMargin(courseTwoStatus.getId(), ConstraintSet.START, 40);

        termCoursesConstraints.connect(courseTwoName.getId(), ConstraintSet.START, courseTwoStatus.getId(), ConstraintSet.END);
        termCoursesConstraints.connect(courseTwoName.getId(), ConstraintSet.TOP, courseOneName.getId(), ConstraintSet.BOTTOM);
        termCoursesConstraints.constrainHeight(courseTwoName.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseTwoName.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseTwoName.getId(), ConstraintSet.START, 35);
//        termCoursesConstraints.setMargin(courseTwoName.getId(), ConstraintSet.TOP, 35);

        ImageView courseThreeStatus = new ImageView(termOneContext);
        courseThreeStatus.setId(generateViewId());
        courseThreeStatus.setImageResource(R.drawable.course_status_planned_15);
        termOneCoursesContainer.addView(courseThreeStatus);

        Button courseThreeName = new Button(termOneContext);
        courseThreeName.setId(generateViewId());
        courseThreeName.setTextColor(courseTextColor);
        courseThreeName.setText("C391 - Almost getting it now ....");
        termOneCoursesContainer.addView(courseThreeName);

        TextView finalCourseBuffer = new TextView(termOneContext);
        finalCourseBuffer.setId(generateViewId());
        termOneCoursesContainer.addView(finalCourseBuffer);

        termCoursesConstraints.connect(courseThreeStatus.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        termCoursesConstraints.connect(courseThreeStatus.getId(), ConstraintSet.TOP, courseTwoName.getId(), ConstraintSet.BOTTOM);
        termCoursesConstraints.constrainHeight(courseThreeStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseThreeStatus.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseThreeStatus.getId(), ConstraintSet.TOP, 43);
        termCoursesConstraints.setMargin(courseThreeStatus.getId(), ConstraintSet.START, 40);
//        termCoursesConstraints.setMargin(courseThreeStatus.getId(), ConstraintSet.BOTTOM, 35); //not working

        termCoursesConstraints.connect(courseThreeName.getId(), ConstraintSet.START, courseThreeStatus.getId(), ConstraintSet.END);
        termCoursesConstraints.connect(courseThreeName.getId(), ConstraintSet.TOP, courseTwoName.getId(), ConstraintSet.BOTTOM);
        termCoursesConstraints.constrainHeight(courseThreeName.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.constrainWidth(courseThreeName.getId(), ConstraintSet.WRAP_CONTENT);
        termCoursesConstraints.setMargin(courseThreeName.getId(), ConstraintSet.START, 35);
//        termCoursesConstraints.setMargin(courseThreeName.getId(), ConstraintSet.TOP, 35);
//        termCoursesConstraints.setMargin(courseThreeName.getId(), ConstraintSet.BOTTOM, 35); //TODO Note the need to add padding on the last item ... Not working :(

        termCoursesConstraints.connect(finalCourseBuffer.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        termCoursesConstraints.connect(finalCourseBuffer.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        termCoursesConstraints.connect(finalCourseBuffer.getId(), ConstraintSet.TOP, courseThreeName.getId(), ConstraintSet.BOTTOM);
        termCoursesConstraints.constrainWidth(finalCourseBuffer.getId(), ConstraintSet.MATCH_CONSTRAINT);
        termCoursesConstraints.setMargin(finalCourseBuffer.getId(), ConstraintSet.TOP, 25);

        termCoursesConstraints.applyTo(termOneCoursesContainer);

        //High-level term two details
        TextView termTwoBackground = new TextView(fragmentContext);
        termTwoBackground.setId(generateViewId());
        termTwoBackground.setBackgroundColor(termBannerBackgroundColor);
        degreePlanContainer.addView(termTwoBackground);

        TextView termTwoName = new TextView(fragmentContext);
        termTwoName.setId(generateViewId());
        termTwoName.setBackgroundColor(termBannerBackgroundColor);
        termTwoName.setTextColor(defaultTextColor);
        termTwoName.setText("Term Two");
        degreePlanContainer.addView(termTwoName);

        TextView termTwoDates = new TextView(fragmentContext);
        termTwoDates.setId(generateViewId());
        termTwoDates.setBackgroundColor(termBannerBackgroundColor);
        termTwoDates.setTextColor(defaultTextColor);
        termTwoDates.setText("2020-07-01 until 2020-06-30");
        degreePlanContainer.addView(termTwoDates);

        ImageButton termTwoEditIcon = new ImageButton(fragmentContext);
        termTwoEditIcon.setId(generateViewId());
        termTwoEditIcon.setBackgroundColor(termBannerBackgroundColor);
        termTwoEditIcon.setImageResource(R.drawable.edit_icon_white);
        degreePlanContainer.addView(termTwoEditIcon);

        constraints.connect(termTwoBackground.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraints.connect(termTwoBackground.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraints.connect(termTwoBackground.getId(), ConstraintSet.TOP, termOneCoursesContainer.getId(), ConstraintSet.BOTTOM);
        constraints.constrainHeight(termTwoBackground.getId(), defaultTextHeight);

        constraints.connect(termTwoName.getId(), ConstraintSet.START, termTwoBackground.getId(), ConstraintSet.START);
        constraints.connect(termTwoName.getId(), ConstraintSet.TOP, termTwoBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(termTwoName.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(termTwoName.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
        constraints.setMargin(termTwoName.getId(), ConstraintSet.START, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp

        constraints.connect(termTwoDates.getId(), ConstraintSet.END, termTwoEditIcon.getId(), ConstraintSet.START);
        constraints.connect(termTwoDates.getId(), ConstraintSet.TOP, termTwoBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(termTwoDates.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(termTwoDates.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
        constraints.setMargin(termTwoDates.getId(), ConstraintSet.START, 20);
        constraints.setMargin(termTwoDates.getId(), ConstraintSet.END, 20);

        constraints.connect(termTwoEditIcon.getId(), ConstraintSet.END, termTwoBackground.getId(), ConstraintSet.END);
        constraints.connect(termTwoEditIcon.getId(), ConstraintSet.TOP, termTwoBackground.getId(), ConstraintSet.TOP);
        constraints.constrainWidth(termTwoEditIcon.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.setMargin(termTwoEditIcon.getId(), ConstraintSet.END, 20);

        constraints.applyTo(degreePlanContainer);

        return degreePlanContainer;
    }
}
