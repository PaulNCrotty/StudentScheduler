package edu.wgu.android.studentscheduler.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.domain.Term;

public class TermFragment extends Fragment {

    private Term term;

    public TermFragment() {
        super(R.layout.term_fragment);
    }

//    public TermFragment(Term term) {
//        super(R.layout.term_fragment);
//        this.term = term;
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ConstraintLayout layout = (ConstraintLayout) view;



//        TextView termNameView = (TextView) layout.getViewById(R.id.termName);
//        termNameView.setText(term.getTermName());
//
//        TextView termDatesView = (TextView) layout.getViewById(R.id.termDates);
//        termDatesView.setText(term.getStartDate() + " until " + term.getEndDate());
//
//        FragmentManager parentFragmentManager = getParentFragmentManager();
//        List<Fragment> fragments = parentFragmentManager.getFragments();
//        int amountOfFragments = fragments.size();
//        if (amountOfFragments > 1) {
//            ConstraintSet constraintSet = new ConstraintSet();
//            TextView background = (TextView) layout.getViewById(R.id.background);
//            ImageView termEditIcon = (ImageView) layout.getViewById(R.id.editTermIcon);
//
//            Fragment penultimateFragment = fragments.get(amountOfFragments - 1);
//            ConstraintLayout previousTermView = (ConstraintLayout)penultimateFragment.getView();
//            TextView previousBackground = (TextView) previousTermView.getViewById(R.id.background);
//            TextView previousTermNameView = (TextView) previousTermView.getViewById(R.id.termName);
//            TextView previousTermDatesView = (TextView) previousTermView.getViewById(R.id.termDates);
//            ImageView previousTermEditIcon = (ImageView) previousTermView.getViewById(R.id.editTermIcon);
//
//            constraintSet.connect(background.getId(), ConstraintSet.TOP, previousBackground.getId(), ConstraintSet.BOTTOM);
//
//            constraintSet.applyTo(layout);
//        }

        return view;

    }


}
