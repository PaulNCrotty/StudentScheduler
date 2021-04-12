package edu.wgu.android.studentscheduler.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.Term;
import edu.wgu.android.studentscheduler.domain.TermStatus;

import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getDateString;

public class TermListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GridLayout termListView = (GridLayout) inflater.inflate(R.layout.term_list_fragment_layout, container, true);

        int parentId = termListView.getId();
        int defaultTextHeight = getResources().getDimensionPixelSize(R.dimen.text_view_term_list_layout_height);
        List<Term> terms = getTerms();
        Context context = getActivity();
        for (Term t : terms) {
            TextView termNameView = new TextView(context, null, R.attr.termNameTextStyle);
            int termNameViewId = View.generateViewId();
            termNameView.setId(termNameViewId);
//            termNameView.setLayoutParams(new GridLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, defaultTextHeight));
            termNameView.setText(t.getTermName());
            termListView.addView(termNameView);

//            ConstraintSet termNameConstraints = new ConstraintSet();
//            termNameConstraints.connect(termNameViewId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
//            termNameConstraints.connect(termNameViewId, ConstraintSet.START, parentId, ConstraintSet.START);
//            termNameConstraints.applyTo(termListView);


            TextView termDatesView = new TextView(context, null, R.attr.termDatesTextStyle);
            String termDates = getString(R.string.fragment_term_dates, t.getStartDate(), t.getEndDate());
            int termDatesViewId = View.generateViewId();
            termDatesView.setId(termDatesViewId);
            termDatesView.setText(termDates);
//            termDatesView.setLayoutParams(new GridLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, defaultTextHeight));
            termListView.addView(termDatesView);

            Log.d("HOW_FAR", "We made it here....");

//            ConstraintSet termDatesConstraints = new ConstraintSet();
//            termDatesConstraints.connect(termDatesViewId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
//            Log.d("HOW_FAR", "and here....");
//            termDatesConstraints.connect(termDatesViewId, ConstraintSet.START, termNameView.getId(), ConstraintSet.END);
//            termDatesConstraints.applyTo(termListView);

            ImageButton editTermDetailsButton = new ImageButton(context, null, R.attr.termEditButtonStyle);
            int editTermButtonId = View.generateViewId();
            Log.d("CONSTRAINTS", "editTermButtonId = " + editTermButtonId);
            editTermDetailsButton.setImageResource(R.drawable.ic_baseline_edit_24);
            termListView.addView(editTermDetailsButton);

//            ConstraintSet editTermButtonConstraints = new ConstraintSet();
//            editTermButtonConstraints.connect(editTermButtonId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
//            editTermButtonConstraints.connect(editTermButtonId, ConstraintSet.END, parentId, ConstraintSet.END);
//            editTermButtonConstraints.applyTo(termListView);
        }


        return termListView;
    }

    //TODO placeholder code for capturing data from and actual persistence store
    private List<Term> getTerms() {
        return Arrays.asList(
                new Term("Term One",
                        getDateString(2020, 7, 1),
                        getDateString(2020, 12, 31),
                        new ArrayList<>(), TermStatus.PAST_COMPLETE));//,
//                new Term("Term Two",
//                        getDateString(2021, 1, 1),
//                        getDateString(2021, 6, 30),
//                        new ArrayList<>(), TermStatus.CURRENT));
    }
}
