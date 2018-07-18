package br.edu.uepb.nutes.haniot.elderly;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.elderly.Accessory;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import br.edu.uepb.nutes.haniot.model.elderly.Medication;
import br.edu.uepb.nutes.haniot.model.dao.ElderlyDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.ui.CustomMultiSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NameColumnsDB;
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

    protected static final String EXTRA_ELDERLY_PIN = "extra_elderly_pin";
    protected static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

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
    CustomMultiSelectSpinner medicationsSpinner;

    @BindView(R.id.accessories_multiSelectSpinner)
    CustomMultiSelectSpinner accessoriesSpinner;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.loading_save_progressBar)
    ProgressBar loadingProgressBar;

    @BindView(R.id.elderly_form_ScrollView)
    ScrollView formScrollView;

    private OnFormListener mListener;
    private Session session;
    private Server server;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String elderlyPin;
    private String elderlyId;
    private ElderlyDAO dao;
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
        server = Server.getInstance(getContext());
        calendar = Calendar.getInstance();
        dao = ElderlyDAO.getInstance(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            elderlyPin = getArguments().getString(ElderlyFormFragment.EXTRA_ELDERLY_PIN);
            elderlyId = getArguments().getString(ElderlyFormFragment.EXTRA_ELDERLY_ID);

            Log.d(TAG, "PIN: " + elderlyPin + " ID: " + elderlyId);
        }

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

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Initializes the UI elements.
     */
    private void initUI() {
        initializeMedications();
        initializeAccessories();
        dateBirthEditText.setOnClickListener(mListenerDatePickerDialog);
        saveButton.setOnClickListener(mListenerSave);

        if (elderlyId != null) {
            elderly = dao.get(elderlyId);
            if (elderly != null) {
                populateForm(elderly);
            } else {
                Toast.makeText(getContext(), R.string.elderly_was_not_select, Toast.LENGTH_SHORT).show();
                mListener.onFormResult(null);
                return;
            }
        } else {
            elderly = new Elderly();
        }
    }

    /**
     * Populate form with elderly data.
     *
     * @param elderlyUp {@link Elderly}
     */
    private void populateForm(Elderly elderlyUp) {
        saveButton.setText(R.string.action_update);

        nameEditText.setText(elderlyUp.getName());
        dateBirthEditText.setText(DateUtils.formatDate(elderlyUp.getDateOfBirth(),
                getString(R.string.date_format)));
        weightEditText.setText(String.valueOf(elderlyUp.getWeight()));
        heightEditText.setText(String.valueOf(elderlyUp.getHeight()));
        phoneEditText.setText(elderlyUp.getPhone());

        if (elderlyUp.getSex() == 0) // male
            ((RadioButton) sexRadioGroup.getChildAt(0)).setChecked(true);
        else // female
            ((RadioButton) sexRadioGroup.getChildAt(1)).setChecked(true);

        if (elderlyUp.getLiveAlone())
            ((RadioButton) liveAloneRadioGroup.getChildAt(0)).setChecked(true);
        else
            ((RadioButton) liveAloneRadioGroup.getChildAt(1)).setChecked(true);

        maritalStatusSpinner.setSelection(elderlyUp.getMaritalStatus());
        educationSpinner.setSelection(elderlyUp.getDegreeOfEducation());

        for (Medication medication : elderlyUp.getMedications()) {
            medicationsSpinner.selection(medication.getName());
        }

        int accessoriesTotal = getResources().getStringArray(R.array.elderly_accessories_array).length;
        for (Accessory accessory : elderlyUp.getAccessories()) {
            if (accessory.getIndex() > -1 && accessory.getIndex() < accessoriesTotal)
                accessoriesSpinner.selection(accessory.getIndex());
            else
                accessoriesSpinner.selection(accessory.getName());
        }
    }

    /**
     * Initializes UI Medications.
     */
    private void initializeMedications() {
        List<String> items = new ArrayList<>();
        items.addAll(getItensExtraSharedPreferences(KEY_ITEMS_MEDICATIONS));

        medicationsSpinner.setItems(items);
        medicationsSpinner.setOnSpinnerListener(new CustomMultiSelectSpinner.OnSpinnerListener() {
            @Override
            public void onMultiItemSelected(@NonNull List<String> items, @NonNull List<Integer> indexItems) {

            }

            @Override
            public void onAddNewItemSuccess(@NonNull String item, @NonNull int indexItem) {
                saveItensExtraSharedPreferences(KEY_ITEMS_MEDICATIONS, item);
            }

            @Override
            public void onAddNewItemCancel() {

            }
        });
    }

    /**
     * Initializes UI Accessories.
     */
    private void initializeAccessories() {
        List<String> items = new ArrayList<>();
        items.addAll(Arrays.asList(getResources().getStringArray(R.array.elderly_accessories_array)));
        items.addAll(getItensExtraSharedPreferences(KEY_ITEMS_ACCESSORIES));

        accessoriesSpinner.setItems(items);
        accessoriesSpinner.setOnSpinnerListener(new CustomMultiSelectSpinner.OnSpinnerListener() {
            @Override
            public void onMultiItemSelected(@NonNull List<String> items, @NonNull List<Integer> indexItems) {

            }

            @Override
            public void onAddNewItemSuccess(@NonNull String item, @NonNull int indexItem) {
                saveItensExtraSharedPreferences(KEY_ITEMS_ACCESSORIES, item);
            }

            @Override
            public void onAddNewItemCancel() {

            }
        });
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
        }

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
     * Listener for event button click save.
     */
    private View.OnClickListener mListenerSave = (v -> {
        Log.d(TAG, "OnClickListener() PIN: " + elderlyPin + " ID: " + elderlyId);

        if (elderlyId == null) save(getElderlyByForm());
        else update(getElderlyByForm());
    });

    /**
     * Retrieve extra items saved in sharedPreferences.
     *
     * @param key {@link String}
     * @return List<String>
     */
    public List<String> getItensExtraSharedPreferences(String key) {
        String extra = session.getString(key);
        List<String> _result = new ArrayList<>();
        for (String s : extra.split(SEPARATOR_ITEMS))
            if (!s.isEmpty()) _result.add(s);

        return _result;
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
    public Elderly getElderlyByForm() {
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

        elderly.clearMedications();
        for (String medication : medicationsSpinner.getSelectedItems()) {
            elderly.addMedication(new Medication(-1, medication));
        }

        elderly.clearAccessories();
        for (String accessory : accessoriesSpinner.getSelectedItems()) {
            int indexAccessory = findIndexArrayResource(accessory, R.array.elderly_accessories_array);
            elderly.addAccessory(new Accessory(indexAccessory, accessory));
        }

        elderly.setUser(session.getUserLogged());
        elderly.setPin(elderlyPin);

        return elderly;
    }

    /**
     * Retrieve the index of the item in an array contained in the resource.
     *
     * @param item
     * @param arrayRes
     * @return int
     */
    private int findIndexArrayResource(String item, @ArrayRes int arrayRes) {
        List<String> _array = Arrays.asList(getResources().getStringArray(arrayRes));
        return _array.indexOf(item);
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

        result.addProperty(NameColumnsDB.ELDERLY_NAME, elderly.getName());
        result.addProperty(NameColumnsDB.ELDERLY_WEIGHT, elderly.getWeight());
        result.addProperty(NameColumnsDB.ELDERLY_HEIGHT, elderly.getHeight());
        result.addProperty(NameColumnsDB.ELDERLY_DATE_BIRTH, elderly.getDateOfBirth());
        result.addProperty(NameColumnsDB.ELDERLY_SEX, elderly.getSex());
        result.addProperty(NameColumnsDB.ELDERLY_PHONE, elderly.getPhone());
        result.addProperty(NameColumnsDB.ELDERLY_LIVE_ALONE, elderly.getLiveAlone());
        result.addProperty(NameColumnsDB.ELDERLY_MARITAL_STATUS, elderly.getMaritalStatus());
        result.addProperty(NameColumnsDB.ELDERLY_DEGREE_EDUCATION, elderly.getDegreeOfEducation());
        result.addProperty(NameColumnsDB.ELDERLY_DEVICE_PIN, elderly.getPin()); //  TODO PIN - Avaliar se eh a entidade idoso

        JsonArray arrayMedications = new JsonArray();
        for (Medication medication : elderly.getMedications()) {
            JsonObject itemMedication = new JsonObject();
            itemMedication.addProperty(NameColumnsDB.ELDERLY_ITEMS_INDEX, medication.getIndex());
            itemMedication.addProperty(NameColumnsDB.ELDERLY_ITEMS_NAME, medication.getName());
            arrayMedications.add(itemMedication);
        }
        result.add(NameColumnsDB.ELDERLY_MEDICATIONS, arrayMedications);

        JsonArray arrayAccessories = new JsonArray();
        for (Accessory accessory : elderly.getAccessories()) {
            JsonObject itemAccessory = new JsonObject();
            itemAccessory.addProperty(NameColumnsDB.ELDERLY_ITEMS_INDEX, accessory.getIndex());
            itemAccessory.addProperty(NameColumnsDB.ELDERLY_ITEMS_NAME, accessory.getName());
            arrayAccessories.add(itemAccessory);
        }
        result.add(NameColumnsDB.ELDERLY_ACCESSORIES, arrayAccessories);

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

            // Save in remote server.
            server.post("users/".concat(session.get_idLogged()).concat("/external"),
                    elderlyToJson(elderly), // json
                    new Server.Callback() {
                        @Override
                        public void onError(JSONObject result) {
                            loading(false);
                            openMessageError();
                        }

                        @Override
                        public void onSuccess(JSONObject result) {
                            loading(false);

                            try {
                                elderly.set_id(result.getString("_id"));
                                dao.save(elderly);  // save in local
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mListener.onFormResult(elderly);
                        }
                    });
        }
    }

    /**
     * Update in local database elderly and remote server.
     *
     * @param elderly {@link Elderly}
     * @return boolean
     */
    public void update(@NonNull Elderly elderly) {
        if (validate(elderly) && mListener != null) {
            loading(true);

            // TODO Implementar o update no servidor!!!
            dao.update(elderly);  // save in local
            mListener.onFormResult(elderly);

            loading(false);

            // Save in remote server.
//            server.put("users/".concat(session.get_idLogged()).concat("/external"),
//                    elderlyToJson(elderly), // json
//                    new Server.Callback() {
//                        @Override
//                        public void onError(JSONObject result) {
//                            loading(false);
//                            openMessageError();
//                        }
//
//                        @Override
//                        public void onSuccess(JSONObject result) {
//                            loading(false);
//
//                            dao.update(elderly);  // save in local
//                            mListener.onFormResult(elderly);
//                        }
//                    });
        }
    }

    /**
     * Show or hide loading.
     *
     * @param enabled boolean
     */
    private void loading(boolean enabled) {
        if (getActivity() == null) return;

        // Force "ScrollView" to bottom page
        formScrollView.post(() -> formScrollView.fullScroll(View.FOCUS_DOWN));

        getActivity().runOnUiThread(() -> {
            if (enabled) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                loadingProgressBar.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
            } else {
                loadingProgressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    /**
     * Open Snackbar message error in remote save.
     */
    private void openMessageError() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            final Snackbar snackbar = Snackbar.make(getView(),
                    R.string.error_internal_device,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.bt_ok, (v) -> {
                snackbar.dismiss();
            });
            snackbar.show();
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
        void onFormResult(Elderly elderly);
    }
}
