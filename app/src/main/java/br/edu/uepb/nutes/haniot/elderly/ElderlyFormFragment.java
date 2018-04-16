package br.edu.uepb.nutes.haniot.elderly;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Accessory;
import br.edu.uepb.nutes.haniot.model.Elderly;
import br.edu.uepb.nutes.haniot.model.Medication;
import br.edu.uepb.nutes.haniot.model.dao.ElderlyDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.ui.MultiSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ElderlyFormFragment implementation.
 * After validation, the activity that calls this fragment must
 * implement the save (Elderly elderly) method to perform the save process.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyFormFragment extends Fragment {
    private final String TAG = "ElderlyFormFragment";

    private final String KEY_ITEMS_MEDICATIONS = "key_medications";
    private final String KEY_ITEMS_ACCESSORIES = "key_accessories";
    private final String SEPARATOR_ITEMS = "#";

    @BindView(R.id.name_editText)
    EditText nameEditText;

    @BindView(R.id.weight_editText)
    EditText weightEditText;

    @BindView(R.id.height_editText)
    EditText heightEditText;

    @BindView(R.id.date_birth_editText)
    EditText dateBirthEditText;

    @BindView(R.id.sex_radioGroup)
    RadioGroup sexRadioGroup;

    @BindView(R.id.phone_editText)
    EditText phoneEditText;

    @BindView(R.id.live_alone_radioGroup)
    RadioGroup liveAloneRadioGroup;

    @BindView(R.id.marital_status_spinner)
    Spinner maritalStatusSpinner;

    @BindView(R.id.education_spinner)
    Spinner educationSpinner;

    @BindView(R.id.medications_multiSelectSpinner)
    MultiSelectSpinner medicationsSpinner;

    @BindView(R.id.accessories_multiSelectSpinner)
    MultiSelectSpinner accessoriesSpinner;

    @BindView(R.id.new_medications_imageButton)
    ImageButton medicationsButton;

    @BindView(R.id.new_accessories_imageButton)
    ImageButton accessoriesButton;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.loading_save_progressBar)
    ProgressBar loadingProgressBar;

    private OnFormListener mListener;
    private Session session;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private Elderly elderly;

    public ElderlyFormFragment() {
        // Required empty public constructor
    }

    public static ElderlyFormFragment newInstance() {
        ElderlyFormFragment fragment = new ElderlyFormFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elderly_form, container, false);
        ButterKnife.bind(this, view);

        session = new Session(getContext());
        calendar = Calendar.getInstance();
        elderly = new Elderly();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ElderlyPinFragment.OnNextPageSelectedListener) {
            mListener = (ElderlyFormFragment.OnFormListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * Initializes the UI elements.
     */
    private void initUI() {
        initializeMedications();
        initializeAccessories();
        dateBirthEditText.setOnClickListener(mListenerDatePickerDialog);
        saveButton.setOnClickListener(mListenerSave);
    }

    /**
     * Initializes UI Medications.
     */
    private void initializeMedications() {
        List<String> items = new ArrayList<>();
        items.addAll(getItensExtraSharedPreferences(KEY_ITEMS_MEDICATIONS));

        medicationsSpinner
                .title(getString(R.string.elderly_select_medications))
                .hint(getResources().getString(R.string.elderly_select_medications))
                .messageEmpty(getString(R.string.elderly_please_add_medications))
                .items(items)
                .build();

        medicationsButton.setOnClickListener(mListenerMedications);
    }

    /**
     * Initializes UI Accessories.
     */
    private void initializeAccessories() {
        List<String> items = new ArrayList<>();
        items.addAll(Arrays.asList(getResources().getStringArray(R.array.elderly_accessories_array)));
        items.addAll(getItensExtraSharedPreferences(KEY_ITEMS_ACCESSORIES));

        accessoriesSpinner
                .title(getString(R.string.elderly_select_accessories))
                .hint(getString(R.string.elderly_select_accessories))
                .items(items)
                .build();

        accessoriesButton.setOnClickListener(mListenerAccessories);
    }

    /**
     * Open dialof datepicker.
     */
    private View.OnClickListener mListenerDatePickerDialog = (v -> {
        int year = calendar.get(Calendar.YEAR) - 60;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (!String.valueOf(dateBirthEditText.getText()).isEmpty()) {
            int[] dateVelues = DateUtils.getDateValues(elderly.getDateOfBirth());
            year = dateVelues[0];
            month = dateVelues[1];
            day = dateVelues[2];
            Log.d(TAG, "elderly.getDateOfBirth() " + elderly.getDateOfBirth());
        }
        Log.d(TAG, "d: " + day + " m: " + month + " y: " + year);
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "onDateSet() d: " + dayOfMonth + " m: " + month + " y: " + year);
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                elderly.setDateOfBirth(calendar.getTimeInMillis());
                dateBirthEditText.setText(DateUtils.calendarToString(calendar, getString(R.string.date_format)));
            }
        }, year, month, day);
        datePickerDialog.show();

        // Close keyboard
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(dateBirthEditText.getWindowToken(), 0);
    });

    /**
     * Open dialog to add new item medication,
     */
    private View.OnClickListener mListenerMedications = (v -> {
        medicationsSpinner.addItemDialog(
                getString(R.string.elderly_add_medications),
                new MultiSelectSpinner.OnItemAddCallback() {
                    @Override
                    public void onSuccess(String item) {
                        Log.d(TAG, "onSuccess() " + item);
                        saveItensExtraSharedPreferences(KEY_ITEMS_MEDICATIONS, item);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel()");
                    }
                });
    });

    /**
     * Open dialog to add new item accessory.
     */
    private View.OnClickListener mListenerAccessories = (v -> {
        accessoriesSpinner.addItemDialog(
                getString(R.string.elderly_add_accessories),
                new MultiSelectSpinner.OnItemAddCallback() {
                    @Override
                    public void onSuccess(String item) {
                        Log.d(TAG, "onSuccess() " + item);
                        saveItensExtraSharedPreferences(KEY_ITEMS_ACCESSORIES, item);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel()");
                    }
                });
    });

    /**
     * Listener for event button click save.
     */
    private View.OnClickListener mListenerSave = (v -> save(getElderyByForm()));

    /**
     * Retrieve extra items saved in sharedPreferences.
     *
     * @param key {@link String}
     * @return List<String>
     */
    public List<String> getItensExtraSharedPreferences(String key) {
        String extra = session.getString(key);
        return Arrays.asList(extra.split(SEPARATOR_ITEMS));
    }

    /**
     * Save extra items in sharedPreferences.
     * Items are saved as {@link String} separated by {@link #SEPARATOR_ITEMS}
     *
     * @param key  {@link String}
     * @param item {@link String}
     * @return boolean
     */
    public boolean saveItensExtraSharedPreferences(String key, String item) {
        if (getItensExtraSharedPreferences(key).size() > 0)
            return session.putString(key, session.getString(key)
                    .concat(SEPARATOR_ITEMS).concat(item));
        return session.putString(key, item);
    }

    /**
     * Construct object with form data.
     *
     * @return {@link Elderly}
     */
    public Elderly getElderyByForm() {
        if (elderly == null) elderly = new Elderly();

        elderly.setName(String.valueOf(nameEditText.getText()));

        String weight = String.valueOf(weightEditText.getText());
        if (!weight.isEmpty())
            elderly.setWeight(Double.valueOf(weight));

        String height = String.valueOf(heightEditText.getText());
        if (!height.isEmpty())
            elderly.setHeight(Integer.valueOf(height));

        elderly.setPhone(String.valueOf(phoneEditText.getText()));

        elderly.setSex(0); // male
        if (sexRadioGroup.getCheckedRadioButtonId() == R.id.gender_female_radioButton)
            elderly.setSex(1); // female

        elderly.setLiveAlone(true);
        if (liveAloneRadioGroup.getCheckedRadioButtonId() == R.id.live_alone_no_radioButton)
            elderly.setLiveAlone(false);

        elderly.setMaritalStatus(maritalStatusSpinner.getSelectedItemPosition());
        elderly.setDegreeOfEducation(educationSpinner.getSelectedItemPosition());

        for (String medication : medicationsSpinner.getSelectedItems()) {
            elderly.addMedication(new Medication(medication));
        }

        for (String accessory : accessoriesSpinner.getSelectedItems()) {
            elderly.addAccessory(new Accessory(accessory));
        }
        elderly.setUser(session.getUserLogged());

        return elderly;
    }

    /**
     * Transform {@link Elderly} in json.
     *
     * @param elderly {@link Elderly}
     * @return {@link String}
     */
    public String elderlyToJson(@NonNull Elderly elderly) {
        if (elderly == null)
            throw new IllegalArgumentException("Parameter elderly cannot be null!");

        JsonObject result = new JsonObject();

        result.addProperty("name", elderly.getName());
        result.addProperty("weight", elderly.getWeight());
        result.addProperty("height", elderly.getHeight());
        result.addProperty("dateOfBirth", elderly.getDateOfBirth());
        result.addProperty("phone", elderly.getPhone());
        result.addProperty("sex", elderly.getSex());
        result.addProperty("liveAlone", elderly.getLiveAlone());
        result.addProperty("maritalStatus", elderly.getMaritalStatus());
        result.addProperty("degreeOfEducation", elderly.getDegreeOfEducation());

        JsonArray arrayMedications = new JsonArray();
        for (Medication medication : elderly.getMedications())
            arrayMedications.add(medication.getName());
        result.add("medications", arrayMedications);

        JsonArray arrayAccessories = new JsonArray();
        for (Accessory accessory : elderly.getAccessories())
            arrayAccessories.add(accessory.getName());
        result.add("accessories", arrayAccessories);

        return String.valueOf(result);
    }

    /**
     * Save in local database elderly and remote server.
     *
     * @param elderly {@link Elderly}
     * @return boolean
     */
    public void save(@NonNull Elderly elderly) {
        if (validate(elderly) && mListener != null) {
            loading(true);
            ElderlyDAO dao = ElderlyDAO.getInstance(getContext());

            final Elderly elderlySaved = dao.save(elderly); // save in local
            if (elderlySaved != null && elderlySaved.getId() > 0) {
                // Save in remote server.
                Server.getInstance(getActivity()).post(
                        "users/".concat(session.get_idLogged()).concat("/external"),
                        elderlyToJson(elderlySaved), // json
                        new Server.Callback() {
                            @Override
                            public void onError(JSONObject result) {
                                loading(false);
                                dao.remove(elderlySaved.getId());
                                openMessageError();
                            }

                            @Override
                            public void onSuccess(JSONObject result) {
                                loading(false);
                                openMessageSuccess(elderlySaved);
                            }
                        });
            }
        }
    }

    /**
     * Show or hide loading.
     *
     * @param enabled boolean
     */
    private void loading(boolean enabled) {
        getActivity().runOnUiThread(() -> {
            loadingProgressBar.requestFocus();
            if (enabled) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
            } else {
                loadingProgressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
            }
        });
    }

    /**
     * Open Snackbar message error in remote save.
     */
    private void openMessageError() {
        final Snackbar snackbar = Snackbar.make(getView(),
                R.string.error_internal_device,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.bt_ok, (v) -> {
            snackbar.dismiss();
        });
        snackbar.show();
    }

    /**
     * Open dialog message success.
     *
     * @param elderly {@link Elderly}
     */
    private void openMessageSuccess(Elderly elderly) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getResources().getString(R.string.elderly_register_success));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                mListener.onFormAssessment(elderly);
            });

            dialog.setNegativeButton(R.string.no_text, (dialogInterface, which) -> {
                mListener.onFormClose();
            });

            dialog.setOnCancelListener((dialogInterface) -> {
                mListener.onFormClose();
            });

            dialog.create().show();
        });
    }


    /**
     * Validate Elderly from data form.
     *
     * @param elderly {@link Elderly}
     * @return boolean
     */
    public boolean validate(Elderly elderly) {
        if (elderly == null) return false;

        if (elderly.getName().isEmpty() || elderly.getName().length() < 3) {
            nameEditText.setError(getResources().getString(R.string.validate_name));
            requestFocus(nameEditText);
            return false;
        } else {
            nameEditText.setError(null);
        }

        if (elderly.getWeight() <= 40D) {
            weightEditText.setError(getResources().getString(R.string.validate_weight));
            requestFocus(weightEditText);
            return false;
        } else {
            weightEditText.setError(null);
        }

        if (elderly.getHeight() <= 100) {
            heightEditText.setError(getResources().getString(R.string.validate_height));
            requestFocus(heightEditText);
            return false;
        } else {
            heightEditText.setError(null);
        }

        if (String.valueOf(dateBirthEditText.getText()).isEmpty()) {
            dateBirthEditText.setError(getResources().getString(R.string.validate_date_birth));
            requestFocus(dateBirthEditText);
            return false;
        } else {
            dateBirthEditText.setError(null);
        }

        if (elderly.getPhone().isEmpty()) {
            phoneEditText.setError(getResources().getString(R.string.validate_phone));
            requestFocus(phoneEditText);
            return false;
        } else {
            phoneEditText.setError(null);
        }

        return true;
    }

    /**
     * Request focus in input
     *
     * @param editText
     */
    private void requestFocus(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public interface OnFormListener {
        void onFormClose();

        void onFormAssessment(Elderly elderly);
    }
}
