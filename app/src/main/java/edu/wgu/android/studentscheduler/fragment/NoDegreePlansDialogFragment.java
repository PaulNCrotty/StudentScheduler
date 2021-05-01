package edu.wgu.android.studentscheduler.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class NoDegreePlansDialogFragment extends DialogFragment {

    Listener listener;

    public interface Listener {
        void onPositive();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement the NoDegreePlansDialogFragment.Listener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("No Degree Plans Found")
                .setMessage("There are you no degree plans for this installation. Would you like to create one?")
                .setPositiveButton("Yes", (dialog, which) -> listener.onPositive())
                .setNegativeButton("Not at this time", null)  //TODO test to make sure dialog closes and activity resumes (user can navigate back to main screen)
                .create();
    }
}
