package edu.wgu.android.studentscheduler.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeneralErrorDialogFragment extends DialogFragment {

    private String title;
    private String message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .create();
    }

}
