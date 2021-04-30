package edu.wgu.android.studentscheduler.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class MissingRequiredValueDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Submission Error")
                .setMessage("Some required values are missing or invalid. Please fill them in and submit again")
                .create();
    }
}
