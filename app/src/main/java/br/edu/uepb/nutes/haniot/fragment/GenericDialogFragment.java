package br.edu.uepb.nutes.haniot.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Mount a Generic AlertDialog. Receiving the title of the titles of the buttons...
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GenericDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final int REQUEST_DIALOG = 2;
    private static final String DIALOG_TAG = "SimpleDialog";
    private static final String ID = "id";
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String BUTTONS = "buttons";
    private static final String ICON = "icon";

    private int mDialogId;
    private static boolean isFragment;

    /**
     * Empty constructor is required for DialogFragment
     */
    public GenericDialogFragment() {
    }

    /**
     * Initializes the AlertDialog.
     *
     * @param id      The id
     * @param title   The id string of title
     * @param message The id string of message
     * @param buttons The buttons
     * @return The GenericDialogFragment
     */
    public static GenericDialogFragment newDialog(int id, int title, int message, int icon, int[] buttons, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putInt(TITLE, title);
        bundle.putInt(MESSAGE, message);
        bundle.putInt(ICON, icon);
        bundle.putIntArray(BUTTONS, buttons);

        GenericDialogFragment dialogFragment = new GenericDialogFragment();
        dialogFragment.setArguments(bundle);

        /**
         * Verifies that who called the dialog was a fragment.
         * The onClick and return is different in Activity
         */
        isFragment = (fragment != null) ? true : false;

        return dialogFragment;
    }

    public static GenericDialogFragment newDialog(int id, int message, int[] buttons, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putInt(MESSAGE, message);
        bundle.putIntArray(BUTTONS, buttons);

        GenericDialogFragment dialogFragment = new GenericDialogFragment();
        dialogFragment.setArguments(bundle);

        /**
         * Verifies that who called the dialog was a fragment.
         * The onClick and return is dirente of Activity
         */
        isFragment = (fragment != null) ? true : false;

        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialogId = getArguments().getInt(ID);
        int[] buttons = getArguments().getIntArray(BUTTONS);

        AlertDialog.Builder alertDiBuilder = new AlertDialog.Builder(getActivity());
        if (getArguments().getInt(TITLE) != 0) {
            alertDiBuilder.setTitle(getArguments().getInt(TITLE));
        }
        if (getArguments().getInt(ICON) != 0) {
            alertDiBuilder.setIcon(getArguments().getInt(ICON));
        }

        alertDiBuilder.setMessage(getArguments().getInt(MESSAGE));

        switch (buttons.length) {
            case 3:
                alertDiBuilder.setNeutralButton(buttons[2], this);
            case 2:
                alertDiBuilder.setNegativeButton(buttons[1], this);
            case 1:
                alertDiBuilder.setPositiveButton(buttons[0], this);
        }

        return alertDiBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int witch) {
        if (isFragment) {
            Intent intent = new Intent();
            intent.putExtra("witch", witch);

            // Calls onActivityResult the targetFragment
            getTargetFragment().onActivityResult(getTargetRequestCode(), AlertDialog.BUTTON_POSITIVE, intent);
        } else {
            Activity activity = getActivity();
            if (activity instanceof OnClickDialogListener) {
                OnClickDialogListener listener = (OnClickDialogListener) activity;
                listener.onClickDialog(mDialogId, witch);
            }
        }
    }

    /**
     * Show Dialog
     *
     * @param supportFragmentManager The FragmentManager
     */
    public void show(FragmentManager supportFragmentManager) {
        Fragment dialogFragment = supportFragmentManager.findFragmentByTag(DIALOG_TAG);
        if (dialogFragment == null) {
            this.show(supportFragmentManager, DIALOG_TAG);
        }
    }

    public interface OnClickDialogListener {
        void onClickDialog(int id, int button);
    }
}
