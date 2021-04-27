package edu.wgu.android.studentscheduler.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmationDialogFragment extends DialogFragment {

    private ConfirmationDialogListener dialogListener;

    public interface ConfirmationDialogListener {

        void onPositive(DialogFragment dialog);

//        void onNegative(DialogFragment dialog);
    }

    /**
     * Ties the invoking activity or fragment (context) to this dialog, which allows the parent context
     * to take its own actions (as defined in its implementation of onPositive and onNegative)
     * in response to the user's choice from the dialog.
     *
     * @param context - the invoking activity or fragment (the parent context)
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ConfirmationDialogListener) {
            dialogListener = (ConfirmationDialogListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ConfirmationDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onPositive(ConfirmationDialogFragment.this);
                    }
                })
                .setNegativeButton("Continue Editing", null
//                        new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogListener.onNegative(ConfirmationDialogFragment.this);
//                    }
//                }
                )
                .create();
    }
}
