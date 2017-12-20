package setlistmanager.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.setlistmanager.R;

import setlistmanager.data.Setlist;

/**
 * Created by User on 17.12.2017.
 */

public class ConfirmDialogFragment extends DialogFragment {

    public interface ConfirmDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    ConfirmDialogListener confirmDialogListener;

    private static String title;
    private static String message;

    public static ConfirmDialogFragment instance( String _title, String _message ) {

        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        title = _title;
        message = _message;

        return confirmDialogFragment;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            confirmDialogListener = (ConfirmDialogListener) activity;

        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.button_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        confirmDialogListener.onDialogPositiveClick(ConfirmDialogFragment.this);

                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        confirmDialogListener.onDialogNegativeClick(ConfirmDialogFragment.this);

                    }
                });

        return builder.create();

    }
}
