package edu.wgu.android.studentscheduler.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.Term;
import edu.wgu.android.studentscheduler.domain.TermStatus;

import static edu.wgu.android.studentscheduler.util.DateTimeUtil.getDateString;

public class TermListFragmentConstraintLayout extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout termListView = (ConstraintLayout) inflater.inflate(R.layout.term_list_fragment_layout_constraint_layout, container, false);

        int parentId = termListView.getId();
        int term_list_fragment_id = R.id.term_list_fragment;
        Log.d("termListView.getId", " (parentId) "+ parentId);
        Log.d("term_list_fragment_id", "(R.id.term_list_fragment) " + term_list_fragment_id);

        int defaultTextHeight = getResources().getDimensionPixelSize(R.dimen.text_view_term_list_layout_height);
        List<Term> terms = getTerms();
        Context context = getContext();
        ConstraintSet constraintSet = new ConstraintSet();
        for (Term t : terms) {
            TextView backgroundBanner = new TextView(context, null, R.attr.termNameTextStyle);
            int backgroundBannerId = View.generateViewId();
            backgroundBanner.setId(backgroundBannerId);
            backgroundBanner.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, defaultTextHeight));
            termListView.addView(backgroundBanner);

            TextView termNameView = new TextView(context, null, R.attr.termNameTextStyle);
            int termNameViewId = View.generateViewId();
            termNameView.setId(termNameViewId);
            termNameView.setText(t.getTermName());
            termNameView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, defaultTextHeight));
            termListView.addView(termNameView);

            TextView termDatesView = new TextView(context, null, R.attr.termDatesTextStyle);
            int termDatesViewId = View.generateViewId();
            termDatesView.setId(termDatesViewId);
            termDatesView.setText(getString(R.string.fragment_term_dates, t.getStartDate(), t.getEndDate()));
            termDatesView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, defaultTextHeight));
            termListView.addView(termDatesView);

            ImageButton editTermDetailsButton = new ImageButton(context, null, R.attr.termEditButtonStyle);
            int editTermButtonId = View.generateViewId();
            editTermDetailsButton.setId(editTermButtonId);
            editTermDetailsButton.setImageResource(R.drawable.ic_baseline_edit_24);
            editTermDetailsButton.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            termListView.addView(editTermDetailsButton);

            constraintSet.connect(termNameViewId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
            constraintSet.connect(termNameViewId, ConstraintSet.START, parentId, ConstraintSet.START);
            constraintSet.constrainWidth(termNameViewId, ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(termNameViewId, defaultTextHeight);

            constraintSet.connect(termDatesViewId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
            constraintSet.connect(termDatesViewId, ConstraintSet.END, editTermButtonId, ConstraintSet.START);
            constraintSet.constrainWidth(termDatesViewId, ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(termDatesViewId, defaultTextHeight);

            constraintSet.connect(editTermButtonId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
            constraintSet.connect(editTermButtonId, ConstraintSet.END, parentId, ConstraintSet.END); //Why is this not working?
            constraintSet.constrainWidth(editTermButtonId, ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(editTermButtonId, ConstraintSet.WRAP_CONTENT);
        }

        constraintSet.applyTo(termListView);

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
