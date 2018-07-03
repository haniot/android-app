package br.edu.uepb.nutes.haniot.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * UI element select spinner.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class SelectSpinner extends AppCompatSpinner {
    private final String TAG = "MultiSelectSpinner";

    private List<String> _items = null;
    private int indexItemSelected;
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private CustomSpinnerAdapter mAdapter;
    private String hintMessage = null;
    private int colorTextItemSelected = 0;
    private OnSelectedListener mListener;

    /**
     * Constructor for use when instantiating directly.
     *
     * @param context
     */
    public SelectSpinner(Context context) {
        super(context);
        this.context = context;
        init();
    }

    /**
     * Constructor used by the layout inflater.
     *
     * @param context
     * @param attrs
     */
    public SelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        Log.d(TAG, "init() multi spinner");
        dialogBuilder = new AlertDialog.Builder(context);
        if (_items == null) _items = new ArrayList<>();

        mAdapter = new CustomSpinnerAdapter(context, android.R.layout.simple_spinner_item, _items);
        super.setAdapter(mAdapter);

    }

    /**
     * Add items.
     *
     * @param items {@link List<String>}
     * @return MultiSelectSpinner
     */
    public SelectSpinner items(@NonNull List<String> items) {
        if (items == null) throw new IllegalArgumentException("List<String> items is required!");

        // Remove title invalid
        items.remove(new String());
        _items = items;

        if (hintMessage != null)
            _items.add(0, hintMessage);

        return this;
    }

    /**
     * Add items.
     *
     * @param items {@link String[]}
     * @return MultiSelectSpinner
     */
    public SelectSpinner items(@NonNull String[] items) {
        if (items == null) throw new IllegalArgumentException("String[] items is required!");

        List<String> result = new ArrayList<>(items.length);
        for (String s : items)
            if (s != null && !s.isEmpty()) result.add(s);

        items(result);
        return this;
    }

    /**
     * Add new item Dynamically.
     *
     * @param item {@link String}
     */
    public void addItem(String item) {
        if (item == null) throw new IllegalArgumentException("Item is null");
        this._items.add(item);
        mAdapter.clear();
        mAdapter.add(item);
        indexItemSelected = _items.size() - 1;
    }


    /**
     * Add message hint.
     *
     * @param hintMessage {@link String}
     * @return MultiSelectSpinner
     */
    public SelectSpinner hint(@Nullable String hintMessage) {
        this.hintMessage = hintMessage;
        return this;
    }

    public SelectSpinner colorTextItemSelected(@ColorInt int colorTextItemSelected) {
        this.colorTextItemSelected = colorTextItemSelected;
        return this;
    }

    public void build() {
        mAdapter.clear();
        if (hintMessage != null) mAdapter.add(hintMessage);

        mAdapter = new CustomSpinnerAdapter(context, android.R.layout.simple_spinner_item, _items);
    }

    /**
     * Clear
     */
    public void clear() {
        build();
    }

    /**
     * Spinner is empty?
     *
     * @return boolean
     */
    public boolean isEmpty() {
        if (_items == null) return true;

        return _items.isEmpty();
    }

    public void addItemDialog(@Nullable String title,
                              OnItemAddCallback callback) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        if (title != null) alertBuilder.setTitle(title);

        /**
         * Creating EditText
         */
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setSingleLine();
        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 40;
        params.rightMargin = 40;
        params.topMargin = 20;
        input.setLayoutParams(params);
        layout.addView(input);

        alertBuilder.setView(layout);

        /**
         * Action add
         */
        alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String valueItem = String.valueOf(input.getText()).trim();
            if (valueItem.isEmpty()) {
                callback.onCancel();
            } else {
                addItem(valueItem);
                callback.onSuccess(valueItem);
                if (mListener != null)
                    mListener.onSelectedItem(valueItem, indexItemSelected);
            }
            closeKeyBoard(input);
        });

        /**
         * Action cancel
         */
        alertBuilder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            closeKeyBoard(input);
            callback.onCancel();
        });

        /**
         * Cancellation action for any other reason.
         */
        alertBuilder.setOnCancelListener((dialog) -> {
            closeKeyBoard(input);
            callback.onCancel();
        });

        alertBuilder.show();
        openKeyBoard(input);
    }

    /**
     * Set OnSelectedListener.
     *
     * @param listener {@link OnSelectedListener}
     */
    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mListener = listener;
    }

    /**
     * Open Keyboard.
     *
     * @param view
     */
    private void openKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Close Keyboad.
     *
     * @param view
     */
    private void closeKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Class Custom SpinnerAdapter.
     */
    public class CustomSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

        private final Context context;
        private List<String> _items;

        public CustomSpinnerAdapter(@NonNull Context context, int textViewResourceId, List<String> _items) {
            super(context, textViewResourceId, _items);
            this.context = context;
            this._items = _items;
        }

        public int getCount() {
            return _items.size();
        }

        public String getItem(int i) {
            return _items.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

//        @Override
//        public View getView(int position, View view, ViewGroup viewgroup) {
//            TextView txt = new TextView(context);
//            txt.setGravity(Gravity.CENTER);
//            txt.setText(_items.get(position));
//            txt.setTextSize(16);
//
//            if (colorSelectedText != 0)
//                txt.setTextColor(colorSelectedText);
//
//            return txt;
//        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER_VERTICAL);
//            txt.setPadding(50, 30, 30, 30);
//            txt.setTextSize(15);
            txt.setText(_items.get(position));
//            txt.setTextColor(Color.BLACK);
//            TextViewCompat.setTextAppearance(txt, android.R.style.TextAppearance_Medium);

            // Set color hint message
            if (hintMessage != null && position == 0) {
                txt.setPadding(40, 30, 30, 0);
                txt.setTextColor(Color.GRAY);
//                ViewCompat.setBackgroundTintList(txt, ColorStateList.valueOf(Color.BLACK));
            }

            return txt;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setText(_items.get(position));
//            txt.setTextSize(16);
            txt.setTextColor(colorTextItemSelected != 0 ? colorTextItemSelected : Color.BLACK);

            return txt;
        }

        @Override
        public boolean isEnabled(int position) {
            // If the spinner has hint, disable it
            return (hintMessage != null && position == 0) ? false : true;
        }
    }

    /**
     * Interface callback Dialog add new item.
     */
    public interface OnItemAddCallback {
        void onSuccess(String item);

        void onCancel();
    }

    public interface OnSelectedListener {
        void onSelectedItem(String item, int indexItem);
    }
}
