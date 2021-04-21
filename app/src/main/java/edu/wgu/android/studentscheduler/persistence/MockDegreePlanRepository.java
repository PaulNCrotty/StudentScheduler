package edu.wgu.android.studentscheduler.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.wgu.android.studentscheduler.entity.Assessment;
import edu.wgu.android.studentscheduler.entity.Course;
import edu.wgu.android.studentscheduler.entity.CourseInstructor;
import edu.wgu.android.studentscheduler.entity.CourseStatus;
import edu.wgu.android.studentscheduler.entity.DegreePlan;
import edu.wgu.android.studentscheduler.entity.Term;
import edu.wgu.android.studentscheduler.entity.TermStatus;

import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getDateString;

public class MockDegreePlanRepository {

    public static final Random RANDOM = new Random(new Date().getTime());
    public static final Calendar CURRENT_TERM_START = getBeginningOfCurrentTerm();
    public static final int MONTHS_PER_TERM = 6;
    private static final int AVG_DAYS_PER_MONTH = 30; // floor (365/12); double a = (365.00/12.00);
    public static final int COURSE_TURNAROUND_BUFFER = 2;

    private static final int MAX_COURSES_PER_TERM = 10;
    private static final int MAX_COURSES_TERMS_PER_PLAN = 10;
    private static final List<String> TERM_NAME_POOL = Arrays.asList("%d Winter Term", "%d Spring Term", "%d Summer Term", "%d Fall Term");

    private static final List<String> COURSE_CODE_POOL = Arrays.asList("C", "D", "F", "G", "M", "R", "S", "T");
    private static final List<String> COURSE_CODE_NUMBER_POOL = Arrays.asList("01", "31", "51", "71", "91");
    private static final List<String> COURSE_NAME_POOL = Arrays.asList("Einstein Electric Math",
            "Ada Lovelace Algorithms", "Bunsen Chemistry", "Curie Bio-Chemistry", "Newton Fall Physics",
            "Clara Schumann Concerts", "Robert Schumann Rhythms", "Granville General Computations",
            "Mozart Music Medley", "Serena Strength Training", "Schwarzenegger Training",
            "Germain Geometry");

    /**
     * Generates a calendar instance at the beginning of a month anywhere from 5 to 0 months from today
     * (0 being the beginning of the actual current date's month).
     *
     * @return Calendar instance at the beginning of a random month up to 5 months prior to today.
     */
    public static Calendar getBeginningOfCurrentTerm() {
        Calendar beginningOfMonth = Calendar.getInstance();
        beginningOfMonth.add(Calendar.MONTH, -(RANDOM.nextInt(MONTHS_PER_TERM)));
        atBeginningOfMonth(beginningOfMonth);
        return beginningOfMonth;
    }

    public static void atBeginningOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public DegreePlan getDegreePlanData() {
        return new DegreePlan(1, "myDegreePlan", getMockTerms());
    }

    protected List<Term> getMockTerms() {
        int amountOTermsInDegreePlan = RANDOM.nextInt(MAX_COURSES_TERMS_PER_PLAN) + 1;
        List<Term> terms = new ArrayList<>(amountOTermsInDegreePlan);

        Calendar termStartDate = getFirstTermStartDate(amountOTermsInDegreePlan);
        for (int i = 0; i < amountOTermsInDegreePlan; i++) {
            String termName = String.format(TERM_NAME_POOL.get(RANDOM.nextInt(TERM_NAME_POOL.size())), termStartDate.get(Calendar.YEAR)); //TODO select term name that makes more sense based upon term dates....
            String startDate = getDateString(termStartDate);
            String endDate = getEndOfTerm(termStartDate);
            TermStatus status = getTermStatus(termStartDate);
            List<Course> mockCourses = getMockCourses(termStartDate);
            terms.add(new Term(termName, startDate, endDate, mockCourses, status));

            toNextTermStartDate(termStartDate); //prepared for next iteration
        }
        return terms;
    }

    protected Calendar getFirstTermStartDate(int amountOTermsInDegreePlan) {
        Calendar beginning = Calendar.getInstance();
        beginning.setTime(CURRENT_TERM_START.getTime()); //effectively clone current term start date
        if(amountOTermsInDegreePlan > 0 && amountOTermsInDegreePlan < 5) {
            if (RANDOM.nextBoolean()) {
                beginning.add(Calendar.MONTH, -MONTHS_PER_TERM); //create a past term in random cases
            }
        } else {
            if (RANDOM.nextBoolean()) {
                beginning.add(Calendar.MONTH, (-2 * MONTHS_PER_TERM)); //create a past term in random cases
            } else if (RANDOM.nextBoolean()) {
                beginning.add(Calendar.MONTH, -MONTHS_PER_TERM); //create a past term in random cases
            }
        }

        atBeginningOfMonth(beginning);

        return beginning;
    }

    protected String getEndOfTerm(Calendar firstDayOfTerm) {
        Calendar endOfTerm = Calendar.getInstance();
        endOfTerm.setTime(firstDayOfTerm.getTime());
        endOfTerm.add(Calendar.MONTH, MONTHS_PER_TERM);
        endOfTerm.add(Calendar.MILLISECOND, -1);
        return getDateString(endOfTerm);
    }

    protected void toNextTermStartDate(Calendar firstDayOfTerm) {
        firstDayOfTerm.add(Calendar.MONTH, MONTHS_PER_TERM);
        atBeginningOfMonth(firstDayOfTerm);
    }

    public TermStatus getTermStatus(Calendar termStartDate) {
        TermStatus status = TermStatus.FUTURE_UNAPPROVED;
        System.out.println("CURRENT_TERM_START: " + getDateString(CURRENT_TERM_START));
        System.out.println("termStartDate: " + getDateString(termStartDate));
        int comparison = CURRENT_TERM_START.compareTo(termStartDate);
        System.out.println("Comparison: " + comparison);
        if (comparison > 0) {
            if ((RANDOM.nextInt(5) + 1) % 5 == 0) {
                // %20 chance that the term is incomplete (not common here at WGU)
                status = TermStatus.PAST_INCOMPLETE;
            } else {
                status = TermStatus.PAST_COMPLETE;
            }
        } else if (comparison == 0) {
            status = TermStatus.CURRENT;
        } else {
            //Some sort of "future" term
            //check to see if upcoming
            Calendar upcomingTerm = Calendar.getInstance();
            upcomingTerm.add(Calendar.MONTH, MONTHS_PER_TERM);
            atBeginningOfMonth(upcomingTerm);

            if (termStartDate.compareTo(upcomingTerm) <= 0) {
                status = TermStatus.FUTURE_APPROVED;
            }
        }

        return status;
    }

    protected List<Course> getMockCourses(Calendar termStartDate) {
        int amountOfCoursesInTerm = RANDOM.nextInt(MAX_COURSES_PER_TERM) + 1;
        List<Course> courses = new ArrayList<>(amountOfCoursesInTerm);
        CourseStatus[] courseStatuses = CourseStatus.values();

        int avgDaysPerCourse = getDaysPerCourse(amountOfCoursesInTerm);

        Calendar courseStartDate = Calendar.getInstance();
        courseStartDate.setTime(termStartDate.getTime());
        for (int i = 0; i < amountOfCoursesInTerm; i++) {
            int nameIndex = RANDOM.nextInt(COURSE_NAME_POOL.size());
            String courseName = COURSE_NAME_POOL.get(nameIndex);
            String courseCode = "" + COURSE_CODE_POOL.get(RANDOM.nextInt(COURSE_CODE_POOL.size())) + nameIndex + COURSE_CODE_NUMBER_POOL.get(RANDOM.nextInt(COURSE_CODE_NUMBER_POOL.size()));
            String startDate = getDateString(courseStartDate);
            String endDate = getDateString(toCourseEndDate(courseStartDate, avgDaysPerCourse));
            List<Assessment> assessments = null; //TODO add fake assessments?
            CourseStatus status = courseStatuses[RANDOM.nextInt(courseStatuses.length)]; //TODO select course status from a smaller pool that make sense based upon specific course dates
            CourseInstructor instructor = null; //TODO add fake instructor?
            List<String> courseNotes = null; //TODO add random notes?

            courses.add(new Course(courseName, courseCode, startDate, endDate, assessments, status, instructor, courseNotes));

            //Prep for next iteration
            courseStartDate.add(Calendar.DAY_OF_MONTH, COURSE_TURNAROUND_BUFFER);

        }
        return courses;
    }

    Calendar toCourseEndDate(Calendar courseStartDate, int daysPerCourse) {
        courseStartDate.add(Calendar.DAY_OF_MONTH, daysPerCourse);
        return courseStartDate;
    }

    /**
     * A simple method to calculate the number of days to be allotted to each course for an even
     * spread
     *
     * @param amountOfCoursesInTerm - the number of courses to be packed into a term
     * @return the number of days to be allotted to each course
     */
    private static int getDaysPerCourse(int amountOfCoursesInTerm) {
        return getDaysPerCourse(amountOfCoursesInTerm, COURSE_TURNAROUND_BUFFER);
    }

    private static int getDaysPerCourse(int amountOfCoursesInTerm, int buffer) {
        return ((AVG_DAYS_PER_MONTH * MONTHS_PER_TERM)/amountOfCoursesInTerm) - buffer;
    }

}
