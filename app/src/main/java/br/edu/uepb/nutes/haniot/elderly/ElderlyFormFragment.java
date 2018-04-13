package br.edu.uepb.nutes.haniot.elderly;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.ui.MultiSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ElderlyFormFragment extends Fragment {
    private final String TAG = "ElderlyFormFragment";
    private final String KEY_ITEMS_MEDICATIONS = "key_medications";
    private final String KEY_ITEMS_ACCESSORIES = "key_accessories";
    private final String SEPARATOR_ITEMS = "#";

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

    private Session session;

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();
    }

    /**
     * Initializes the UI elements.
     */
    private void initUI() {
        initializeMedications();
        initializeAccessories();
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Opens dialog to add new item.
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
     * Opens dialog to add new item.
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
}
