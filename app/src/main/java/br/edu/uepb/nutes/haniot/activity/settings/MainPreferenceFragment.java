package br.edu.uepb.nutes.haniot.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.UserRegisterActivity;
import br.edu.uepb.nutes.haniot.activity.PilotStudyActivity;
import br.edu.uepb.nutes.haniot.activity.account.ChangePasswordActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.Synchronize;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.devices.register.DeviceManagerActivity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * MainPreferenceFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MainPreferenceFragment extends PreferenceFragment {
    public static final String FORM_UPDATE = "form_update";

    private AppPreferencesHelper appPreferences;
    private Repository mRepository;
    private CompositeDisposable mComposite;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        appPreferences = AppPreferencesHelper.getInstance(getActivity().getApplicationContext());
        mRepository = Repository.getInstance(getActivity().getApplicationContext());
        mComposite = new CompositeDisposable();
        // Send feedback
        Preference prefSendFeedback = findPreference(getString(R.string.key_send_bug));
        prefSendFeedback.setOnPreferenceClickListener(preference -> {
            sendContact(getActivity());
            return true;
        });

        // Sign Out
        Preference prefSignout = findPreference(getString(R.string.key_signout));
        prefSignout.setOnPreferenceClickListener(preference -> {

            // Dialog - confirm sign out.
            new AlertDialog
                    .Builder(getActivity())
                    .setMessage(R.string.confirm_sign_out)
                    .setPositiveButton(R.string.bt_ok, (dialog, which) -> {
                        // Disconnect user from application and redirect to login screen
                        if (appPreferences.removeUserLogged()) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), R.string.error_sign_out, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.bt_cancel, null)
                    .show();
            return true;
        });

        // Your data
        Preference prefYourData = findPreference(getString(R.string.key_your_data));
        prefYourData.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), UserRegisterActivity.class);
            intent.putExtra("editUser", true);
            intent.putExtra("action", true);

            getActivity().startActivity(intent);
            return true;
        });

        // Your password
        Preference prefYourPassword = findPreference(getString(R.string.key_your_password));
        prefYourPassword.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);

            getActivity().startActivity(intent);
            return true;
        });

        // Manager devices
        Preference prefDeviceManager = findPreference(getString(R.string.key_manager_device));
        prefDeviceManager.setOnPreferenceClickListener(preference -> {
            getActivity().startActivity(new Intent(getActivity(), DeviceManagerActivity.class));
            return true;
        });

        // Manager Pilot Study
        Preference prefPilotStudy = findPreference(getString(R.string.key_pilot_study));
        prefPilotStudy.setOnPreferenceClickListener(preference -> {
            getActivity().startActivity(new Intent(getActivity(), PilotStudyActivity.class));
            return true;
        });

        // Manager Pilot Study
        Preference prefDeleteAccount = findPreference(getString(R.string.key_delete_account));
        prefDeleteAccount.setOnPreferenceClickListener(preference -> {
            // Dialog - confirm delete account.
            new AlertDialog
                    .Builder(getActivity())
                    .setMessage(R.string.confirm_delete_account)
                    .setPositiveButton(R.string.bt_ok, (dialog, which) -> {
                                // Remove user from server and redirect to login screen
                                mComposite.add(mRepository
                                        .deleteUserById(appPreferences.getUserLogged().get_id())
                                        .subscribe(() -> {
                                            if (appPreferences.removeUserLogged()) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                getActivity().startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }, throwable -> {
                                            Toast.makeText(getActivity(), R.string.error_sign_out, Toast.LENGTH_SHORT).show();
                                        }));
                            }
                    )
                    .setNegativeButton(R.string.bt_cancel, null)
                    .show();
            return true;
        });

        // Manager Pilot Study
        Preference prefMeasurements = findPreference(getString(R.string.key_monitor_measurements));
        prefMeasurements.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra(SettingsActivity.SETTINGS_TYPE, SettingsActivity.SETTINGS_MEASUREMENTS);
            getActivity().startActivity(intent);
            return true;
        });

        Preference prefSync = findPreference(getString(R.string.key_sync));
        prefSync.setOnPreferenceClickListener(preference -> {
            Synchronize.getInstance(getActivity().getApplicationContext()).synchronize(true);
            return true;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mComposite.dispose();
    }

    /**
     * Email client intent to send support email.
     * Appends the necessary device information to email body useful when providing support
     *
     * @param context {@link Context}
     */
    public void sendContact(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\n" + context.getString(R.string.message_not_remove)
                    + "\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email_contact)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "HANIoT - Android App [BUG]");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
