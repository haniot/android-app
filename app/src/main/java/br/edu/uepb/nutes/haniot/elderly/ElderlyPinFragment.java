package br.edu.uepb.nutes.haniot.elderly;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ElderlyPinFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyPinFragment extends Fragment {

    private OnNextPageSelectedListener mListener;

    @BindView(R.id.pin_editText)
    TextInputEditText pinEditText;

    @BindView(R.id.next_page_button)
    Button nextPageButton;

    public ElderlyPinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElderlyPinFragment.
     */
    public static ElderlyPinFragment newInstance() {
        ElderlyPinFragment fragment = new ElderlyPinFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_elderly_pin, container, false);
        ButterKnife.bind(this, view);

        nextPageButton.setOnClickListener(v -> associatePin());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pinEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) associatePin();
            return false;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextPageSelectedListener) {
            mListener = (OnNextPageSelectedListener) context;
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
     * Validate Elderly from data form.
     *
     * @return boolean
     */
    public boolean validate() {
        if (String.valueOf(pinEditText.getText()).isEmpty()) {
            pinEditText.setError(getResources().getString(R.string.required_field));
            requestFocus(pinEditText);
            return false;
        } else {
            pinEditText.setError(null);
        }

        if (pinEditText.getText().length() != 4) {
            pinEditText.setError(getResources().getString(R.string.validate_pin));
            requestFocus(pinEditText);
            return false;
        } else {
            pinEditText.setError(null);
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

    private void associatePin() {
        // TODO realizar o processo de associacao de PIN.
        // TODO Alguma requisição no servidor para verificação!?

        if (validate())
            mListener.onNextPageSelected(String.valueOf(pinEditText.getText()));
    }

    public interface OnNextPageSelectedListener {
        void onNextPageSelected(String pin);
    }
}
