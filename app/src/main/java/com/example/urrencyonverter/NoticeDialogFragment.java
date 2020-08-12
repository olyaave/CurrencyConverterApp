package com.example.urrencyonverter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class NoticeDialogFragment extends DialogFragment{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);

        builder.setMessage(getTag())
                .setPositiveButton(R.string.try_again, (dialog, id) -> mListener.onDialogPositiveClick(NoticeDialogFragment.this))
                .setNegativeButton(R.string.close_app, (dialog, id) -> mListener.onDialogNegativeClick(NoticeDialogFragment.this));
        return builder.create();
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}