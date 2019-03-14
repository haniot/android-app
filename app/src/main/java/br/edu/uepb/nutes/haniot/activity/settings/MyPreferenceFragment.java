package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.UpdateDataActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.devices.register.DeviceManagerActivity;

/**
 * MyPreferenceFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class MyPreferenceFragment extends PreferenceFragment {
    public static final String FORM_UPDATE = "form_update";

    AlertDialog.Builder alertDialogBuilder;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Send feedback
        Preference prefSendFeedback = findPreference(getString(R.string.key_send_feedback));
        prefSendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                sendFeedback(getActivity());
                return true;
            }
        });

        // Sign Out
        Preference prefSignout = findPreference(getString(R.string.key_signout));
        prefSignout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                /**
                 * Dialog - confirm sign out.
                 */
                alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(getActivity().getString(R.string.confirm_sign_out));

                alertDialogBuilder.setPositiveButton(R.string.bt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        /**
                         * Disconnect user from application and redirect to login screen
                         */
                        Session session = new Session(getActivity());
                        if (session.removeLogged()) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), R.string.error_sign_out, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.bt_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialogBuilder.create().show();

                return true;
            }
        });

        // Your data
        Preference prefYourdata = findPreference(getString(R.string.key_your_data));
        prefYourdata.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), UpdateDataActivity.class);
                intent.putExtra(FORM_UPDATE, true);

                getActivity().startActivity(intent);
                getActivity().finish();

                return true;
            }
        });

        // Manager devices data
        Preference prefDeviceManager = findPreference(getString(R.string.key_manager_device));
        prefDeviceManager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startActivity(new Intent(getActivity(), DeviceManagerActivity.class));
                return true;
            }
        });
    }

    /**
     * Email client intent to send support email.
     * Appends the necessary device information to email body useful when providing support
     *
     * @param context
     */
    public void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\n" + context.getString(R.string.message_not_remove) + "\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email_contact)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "From android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
