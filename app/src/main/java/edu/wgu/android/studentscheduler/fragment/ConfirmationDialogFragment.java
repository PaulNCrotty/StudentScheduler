package edu.wgu.android.studentscheduler.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class ConfirmationDialogFragment extends DialogFragment {

    private int key;
    private String title;
    private String message;
    private String positiveButton;
    private String negativeButton;

    private ConfirmationDialogListener dialogListener;

    public ConfirmationDialogFragment() {
        this.key = 0;
        this.title = "Confirm Leaving";
        this.message = "Any changes will be lost. Are you sure you want to quit?";
        this.positiveButton = "Yes";
        this.negativeButton = "Continue Editing";
    }

    public ConfirmationDialogFragment(int key, String title, String message, String positiveButton, String negativeButton) {
        this.key = key;
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
    }

    public interface ConfirmationDialogListener {

        void onPositive(int key);

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
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> dialogListener.onPositive(key))
                .setNegativeButton(negativeButton, null)
                .create();
    }
}
