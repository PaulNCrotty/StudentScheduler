package edu.wgu.android.studentscheduler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.DegreePlan;
import edu.wgu.android.studentscheduler.domain.assessment.Assessment;
import edu.wgu.android.studentscheduler.domain.course.Course;
import edu.wgu.android.studentscheduler.domain.course.CourseStatus;
import edu.wgu.android.studentscheduler.domain.term.Term;

import static android.view.View.generateViewId;

public class DegreePlanActivity extends StudentSchedulerActivity {

//    private static final int MODIFY_TERM_RESULT = 0;

    private static final Map<CourseStatus, Integer> COURSE_STATUS_MAP = getCourseStatusMap();

    private long degreePlanId;
    private DegreePlan degreePlan;

    public DegreePlanActivity() {
        super(R.layout.activity_degree_plan);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // pull degree plan
        if (savedInstanceState != null) {
            degreePlanId = savedInstanceState.getLong(DEGREE_PLAN_ID_BUNDLE_KEY);
        } else {
            degreePlanId = getIntent().getExtras().getLong(DEGREE_PLAN_ID_BUNDLE_KEY);
        }
        degreePlan = getDegreePlan(degreePlanId);

        ((TextView) findViewById(R.id.degreePlanDetailsMainHeader)).setText(getString(R.string.degree_plan_title, degreePlan.getStudentName()));
        ((TextView) findViewById(R.id.degreePlanDetailsSubtitle)).setText(getString(R.string.degree_plan_subtitle_read_only, degreePlan.getName()));

        ConstraintLayout degreePlanContainer = findViewById(R.id.degree_plan_container);
        Context degreePlanContainerContext = degreePlanContainer.getContext();
        int defaultTextHeight = 100; //getResources().getDimensionPixelSize(R.dimen.text_view_term_list_layout_height);
        int termBannerBackgroundColor = Color.parseColor("#3700B3");
        int courseContainerBackgroundColor = Color.parseColor("#DEDEDE");
        int defaultTextColor = Color.parseColor("#FFFFFF");
        int courseTextColor = Color.parseColor("#3700B3");

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(degreePlanContainer);

        int connectionId = degreePlanContainer.getId();
        for (Term term : degreePlan.getTerms()) {
            //High-level term one details
            TextView background = new TextView(degreePlanContainerContext);
            background.setId(generateViewId());
            background.setBackgroundColor(termBannerBackgroundColor);
            degreePlanContainer.addView(background);

            TextView title = new TextView(degreePlanContainerContext);
            title.setId(generateViewId());
            title.setBackgroundColor(termBannerBackgroundColor);
            title.setTextColor(defaultTextColor);
            title.setText(term.getName());
            degreePlanContainer.addView(title);

            TextView dates = new TextView(degreePlanContainerContext);
            dates.setId(generateViewId());
            dates.setBackgroundColor(termBannerBackgroundColor);
            dates.setTextColor(defaultTextColor);
            dates.setText(getString(R.string.start_and_end_dates, term.getStartDate(), term.getEndDate()));
            degreePlanContainer.addView(dates);

//            ImageButton editIcon = new ImageButton(degreePlanContainerContext);
//            editIcon.setId(generateViewId());
//            editIcon.setBackgroundColor(termBannerBackgroundColor);
////            editIcon.setOnClickListener(new ModifyTermAction(term));
//            editIcon.setImageResource(R.drawable.edit_icon_white);
//            degreePlanContainer.addView(editIcon);

            constraints.connect(background.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraints.connect(background.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            if (connectionId == degreePlanContainer.getId()) {
                constraints.connect(background.getId(), ConstraintSet.TOP, connectionId, ConstraintSet.TOP);
            } else {
                constraints.connect(background.getId(), ConstraintSet.TOP, connectionId, ConstraintSet.BOTTOM);
            }
            constraints.constrainHeight(background.getId(), defaultTextHeight);

            constraints.connect(title.getId(), ConstraintSet.START, background.getId(), ConstraintSet.START);
            constraints.connect(title.getId(), ConstraintSet.TOP, background.getId(), ConstraintSet.TOP);
            constraints.constrainWidth(title.getId(), ConstraintSet.WRAP_CONTENT);
            constraints.setMargin(title.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
            constraints.setMargin(title.getId(), ConstraintSet.START, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp

//            constraints.connect(dates.getId(), ConstraintSet.END, editIcon.getId(), ConstraintSet.START);
            constraints.connect(dates.getId(), ConstraintSet.END, background.getId(), ConstraintSet.END);
            constraints.connect(dates.getId(), ConstraintSet.TOP, background.getId(), ConstraintSet.TOP);
            constraints.constrainWidth(dates.getId(), ConstraintSet.WRAP_CONTENT);
            constraints.setMargin(dates.getId(), ConstraintSet.TOP, 20); //perhaps the style will still take effect; otherwise, we'll need some dimens in dp
            constraints.setMargin(dates.getId(), ConstraintSet.START, 20);
            constraints.setMargin(dates.getId(), ConstraintSet.END, 20);

//            constraints.connect(editIcon.getId(), ConstraintSet.END, background.getId(), ConstraintSet.END);
//            constraints.connect(editIcon.getId(), ConstraintSet.TOP, background.getId(), ConstraintSet.TOP);
//            constraints.constrainWidth(editIcon.getId(), ConstraintSet.WRAP_CONTENT);
//            constraints.setMargin(editIcon.getId(), ConstraintSet.END, 20);

            //Course Containers and Details
            ConstraintLayout courseContainer = new ConstraintLayout(degreePlanContainerContext);
            courseContainer.setId(generateViewId());
            courseContainer.setBackgroundColor(courseContainerBackgroundColor);

            degreePlanContainer.addView(courseContainer);

            constraints.connect(courseContainer.getId(), ConstraintSet.START, background.getId(), ConstraintSet.START);
            constraints.connect(courseContainer.getId(), ConstraintSet.END, background.getId(), ConstraintSet.END);
            constraints.connect(courseContainer.getId(), ConstraintSet.TOP, background.getId(), ConstraintSet.BOTTOM); //begin where termOneBackground border ends (vertically)
            constraints.constrainHeight(courseContainer.getId(), ConstraintSet.WRAP_CONTENT);

            ConstraintSet coursesConstraints = new ConstraintSet();
            coursesConstraints.clone(courseContainer);
            Context termContext = courseContainer.getContext();
            List<Course> courses = term.getCourses();
            int previousCourseId = courseContainer.getId(); //set first course top relative to container
            if (courses.size() <= 0) {
//                Button createCourseButton = new Button(termContext);
//                createCourseButton.setId(generateViewId());
//                createCourseButton.setTextColor(courseTextColor);
//                createCourseButton.setText("Create New Course");
//                createCourseButton.setOnClickListener(v -> showCourseDetailsActivity(term, 0));
//                courseContainer.addView(createCourseButton);
                TextView createCourseButton = new TextView(termContext);
                createCourseButton.setId(generateViewId());
                createCourseButton.setTextColor(courseTextColor);
                createCourseButton.setText(getString(R.string.no_courses_found));
                courseContainer.addView(createCourseButton);

                coursesConstraints.connect(createCourseButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                coursesConstraints.connect(createCourseButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                coursesConstraints.constrainHeight(createCourseButton.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.setMargin(createCourseButton.getId(), ConstraintSet.START, 40);
            }

            for (Course course : courses) {
                ImageView courseStatus = new ImageView(termContext);
                courseStatus.setId(generateViewId());
                courseStatus.setImageResource(COURSE_STATUS_MAP.get(course.getStatus()));
                courseContainer.addView(courseStatus);

                TextView courseTitle = new TextView(termContext);
                courseTitle.setId(generateViewId());
                courseTitle.setTextColor(courseTextColor);
                courseTitle.setText(getString(R.string.course_title, course.getCourseCode(), course.getCourseName()));
//                courseTitle.setOnClickListener(v -> showCourseDetailsActivity(term, course.getId()));
                courseContainer.addView(courseTitle);

                TextView courseEndDate = new TextView(termContext);
                courseEndDate.setId(generateViewId());
                courseEndDate.setTextColor(courseTextColor);
                courseEndDate.setText(course.getEndDate());
                courseContainer.addView(courseEndDate);

                coursesConstraints.connect(courseStatus.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                coursesConstraints.constrainHeight(courseStatus.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.constrainWidth(courseStatus.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.setMargin(courseStatus.getId(), ConstraintSet.TOP, 28);
                coursesConstraints.setMargin(courseStatus.getId(), ConstraintSet.START, 40);

                coursesConstraints.connect(courseTitle.getId(), ConstraintSet.START, courseStatus.getId(), ConstraintSet.END);
                coursesConstraints.constrainHeight(courseTitle.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.constrainWidth(courseTitle.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.setMargin(courseTitle.getId(), ConstraintSet.START, 40);
                coursesConstraints.setMargin(courseTitle.getId(), ConstraintSet.TOP, 35);

                coursesConstraints.connect(courseEndDate.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                coursesConstraints.constrainHeight(courseEndDate.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.constrainWidth(courseEndDate.getId(), ConstraintSet.WRAP_CONTENT);
                coursesConstraints.setMargin(courseEndDate.getId(), ConstraintSet.TOP, 35);
                coursesConstraints.setMargin(courseEndDate.getId(), ConstraintSet.END, 30);

                if (previousCourseId == courseContainer.getId()) {
                    coursesConstraints.connect(courseStatus.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    coursesConstraints.connect(courseTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    coursesConstraints.connect(courseEndDate.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                } else {
                    coursesConstraints.connect(courseStatus.getId(), ConstraintSet.TOP, previousCourseId, ConstraintSet.BOTTOM);
                    coursesConstraints.connect(courseTitle.getId(), ConstraintSet.TOP, previousCourseId, ConstraintSet.BOTTOM);
                    coursesConstraints.connect(courseEndDate.getId(), ConstraintSet.TOP, previousCourseId, ConstraintSet.BOTTOM);
                }

                previousCourseId = courseTitle.getId(); // prep for next course iteration
            }
            coursesConstraints.applyTo(courseContainer);

            connectionId = courseContainer.getId(); // prep for next term iteration

        }

        constraints.applyTo(degreePlanContainer);
    }

    public void loadStudentAssessments(View view) {
        Intent intent = new Intent(this, StudentAssessmentListActivity.class);
        List<Assessment> allAssessments = new ArrayList<>();
        for (Term t : degreePlan.getTerms()) {
            for(Course c: t.getCourses()) {
                allAssessments.addAll(c.getAssessments());
            }
        }

        intent.putExtra(DEGREE_PLAN_STUDENT_NAME_BUNDLE_KEY, degreePlan.getStudentName());
        intent.putExtra(ASSESSMENT_ARRAY_BUNDLE_KEY, (Serializable) allAssessments);

        startActivity(intent);
    }

    private static Map<CourseStatus, Integer> getCourseStatusMap() {
        Map<CourseStatus, Integer> courseStatusMap = new HashMap<>(CourseStatus.values().length);
        courseStatusMap.put(CourseStatus.PLANNED, R.drawable.course_status_planned_15);
        courseStatusMap.put(CourseStatus.DROPPED, R.drawable.course_status_dropped_15);
        courseStatusMap.put(CourseStatus.ENROLLED, R.drawable.course_status_enrolled_15);
        courseStatusMap.put(CourseStatus.IN_PROGRESS, R.drawable.course_status_in_progress_15);
        courseStatusMap.put(CourseStatus.PASSED, R.drawable.course_status_passed_15);
        courseStatusMap.put(CourseStatus.FAILED, R.drawable.course_status_failed_15);

        return courseStatusMap;
    }

    private DegreePlan getDegreePlan(long degreePlanId) {
        return repositoryManager.getDegreePlanData(degreePlanId);
    }
}
