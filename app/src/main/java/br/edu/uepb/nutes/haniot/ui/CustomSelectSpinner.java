package br.edu.uepb.nutes.haniot.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;

public class CustomSelectSpinner extends LinearLayout {
    private final String TAG = "CustomSelectSpinner";

    protected final Context context;
    protected OnSpinnerListener mListener;
    protected AppCompatSpinner mSpinner;
    protected int indexItemSelected;

    protected List<String> items;

    protected CustomSpinnerAdapter mAdapter;
    protected LinearLayout boxButton;
    protected ImageButton mButton;

    protected String hint;
    protected String titleDialogAddNewItem;
    @ColorInt
    protected int colorSelectedText;
    @ColorInt
    protected int colorBackgroundTint;
    protected boolean enabledAddNewItem;

    public CustomSelectSpinner(Context context) {
        super(context);
        this.context = context;

        initControl();
    }

    public CustomSelectSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initControl();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSelectSpinner);
        if (typedArray != null || typedArray.length() > 0) {
            try {
                setItems(typedArray.getTextArray(R.styleable.CustomSelectSpinner_android_entries));
                setHint(typedArray.getString(R.styleable.CustomSelectSpinner_android_hint));
                setColorSelectedText(typedArray.getColor(R.styleable.CustomSelectSpinner_colorSelectedText, Color.GRAY));
                setColorBackgroundTint(typedArray.getColor(R.styleable.CustomSelectSpinner_colorBackgroundTint, Color.GRAY));
                setEnabledAddNewItem(typedArray.getBoolean(R.styleable.CustomSelectSpinner_colorBackgroundTint, true));
            } finally {
                typedArray.recycle();
            }
        }
    }

    /**
     * Load xml layout and elements.
     */
    private void initControl() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ui_custom_spinner_layout, this);

        this.indexItemSelected = -1;
        this.enabledAddNewItem = true;
        this.items = new ArrayList<>();
        this.hint = context.getResources().getString(R.string.survey_select_an_answer);
        this.titleDialogAddNewItem = context.getResources().getString(R.string.survey_add_new_item);

        assignUiElements();
        assignListeners();
        initAdapter();
    }

    /**
     * get instance elements.
     */
    private void assignUiElements() {
        this.mSpinner = (AppCompatSpinner) findViewById(R.id.custom_select_spinner);
        this.boxButton = (LinearLayout) findViewById(R.id.custom_box_add_item);
        this.mButton = (ImageButton) findViewById(R.id.custom_add_item_imageButton);
    }

    /**
     * init handlers
     */
    private void assignListeners() {
        if (this.enabledAddNewItem) mButton.setOnClickListener(v -> openDialogAddNewItem());

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && mListener != null)
                    mListener.onItemSelected(String.valueOf(parent.getItemAtPosition(position)), position - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Init adapter spinner.
     */
    private void initAdapter() {
        this.mAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, getItems());
        this.mSpinner.setAdapter(this.mAdapter);

        refreshComponent();
    }

    /**
     * Refresh components.
     */
    private void refreshComponent() {
        if (this.mSpinner == null) return;

        ViewCompat.setBackgroundTintList(this.mSpinner, ColorStateList.valueOf(getColorBackgroundTint()));
        this.mButton.setColorFilter(getColorBackgroundTint());

        if (!this.enabledAddNewItem) this.boxButton.setVisibility(View.GONE);
        else this.boxButton.setVisibility(View.VISIBLE);

        refreshAdapter();
    }

    /**
     * Refresh adapter spinner.
     */
    private void refreshAdapter() {
        this.mAdapter.clear();
        this.mAdapter.addAll(getItems());
    }

    /**
     * Add new item Dynamically.
     *
     * @param item {@link String}
     */
    private void addItem(String item) {
        if (item == null) throw new IllegalArgumentException("item is required! cannot be null.");
        this.items.add(item);

        this.indexItemSelected = this.items.size() - 2;
        this.mSpinner.setSelection(this.indexItemSelected + 1);
        if (this.mListener != null) this.mListener.onItemSelected(item, this.indexItemSelected);

        refreshAdapter();
    }

    /**
     * Clear adapter spinner.
     */
    public void clear() {
        this.mAdapter.clear();
        refreshAdapter();
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
        addHintInItems();
    }

    public void setItems(CharSequence[] entries) {
        this.items = new ArrayList<>();
        if (entries == null) return;

        for (CharSequence c : entries)
            this.items.add(String.valueOf(c));

        addHintInItems();
    }

    public String getHint() {
        return this.hint;
    }

    public void setHint(String hint) {
        if (this.hint != null) this.hint = hint;
        addHintInItems();
    }

    public int getColorSelectedText() {
        return this.colorSelectedText;
    }

    public void setColorSelectedText(int colorSelectedText) {
        this.colorSelectedText = colorSelectedText;
        refreshComponent();
    }

    public int getColorBackgroundTint() {
        return this.colorBackgroundTint;
    }

    public void setColorBackgroundTint(int colorBackgroundTint) {
        this.colorBackgroundTint = colorBackgroundTint;
        refreshComponent();
    }

    public int getIndexItemSelected() {
        return this.indexItemSelected;
    }

    public String getItemSelected() {
        if (this.indexItemSelected >= 0 && (this.indexItemSelected + 1) <= this.items.size()) {
            if (this.items.contains(new String(getHint())))
                return this.items.get(this.indexItemSelected + 1);
            else return this.items.get(this.indexItemSelected);
        }
        return null;
    }

    /**
     * Add hint in the list of items.
     */
    private void addHintInItems() {
        if (this.items == null || this.hint == null) return;

        this.items.remove(new String(getHint()));
        this.items.add(0, getHint());
        refreshComponent();
    }

    public void setEnabledAddNewItem(boolean enabled) {
        this.enabledAddNewItem = enabled;
        refreshComponent();
    }

    /**
     * Set {@link OnSpinnerListener}
     *
     * @param listener
     */
    public void setOnSpinnerListener(OnSpinnerListener listener) {
        this.mListener = listener;
    }

    /**
     * Select item in list.
     *
     * @param index
     */
    public void selection(int index) {
        if (index >= 0 && (index + 1) <= this.items.size()) {
            this.indexItemSelected = index;
            this.mSpinner.setSelection(index + 1);
        }
    }


    /**
     * open dialog to add new item.
     */
    public void openDialogAddNewItem() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        if (this.titleDialogAddNewItem != null) alertBuilder.setTitle(this.titleDialogAddNewItem);

        /**
         * Creating EditText
         */
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setSingleLine();
        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            if (valueItem.isEmpty() || this.items.contains(valueItem)) {
                if (this.mListener != null) this.mListener.onAddNewItemCancel();
            } else {
                addItem(valueItem);
                if (this.mListener != null)
                    this.mListener.onAddNewItemSuccess(valueItem, this.indexItemSelected);
            }
        });

        /**
         * Action cancel
         */
        alertBuilder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            if (this.mListener != null) this.mListener.onAddNewItemCancel();
        });

        /**
         * Cancellation action for any other reason.
         */
        alertBuilder.setOnCancelListener(dialog -> {
            if (this.mListener != null) this.mListener.onAddNewItemCancel();
        });

        alertBuilder.setOnDismissListener(dialog -> {
            closeKeyBoard();
        });

        alertBuilder.show();
        openKeyBoard(input);
    }

    /**
     * Open Keyboard.
     *
     * @param view
     */
    private void openKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Close Keyboad.
     */
    private void closeKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) (this.context)
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("indexItemSelected", getIndexItemSelected());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("superState");
            selection(bundle.getInt("indexItemSelected"));
        }
        super.onRestoreInstanceState(state);
    }

    class CustomSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
        private final Context context;
        private List<String> _items;

        public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> _items) {
            super(context, resource, _items);
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

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(_items.get(position));
            TextViewCompat.setTextAppearance(txt, android.R.style.TextAppearance_Medium);

            // Set color hint message
            if (position == 0) {
                txt.setPadding(40, 30, 50, 0);
                txt.setTextColor(Color.GRAY);
            } else {
                txt.setPadding(50, 30, 50, 30);
                txt.setTextColor(Color.BLACK);
            }

            return txt;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setText(_items.get(position));
            txt.setTextSize(16);
            txt.setTextColor(colorSelectedText);

            return txt;
        }

        @Override
        public boolean isEnabled(int position) {
            // If the spinner has hint, disable it
            return (position == 0) ? false : true;
        }
    }

    /**
     * Listener spinner.
     */
    public interface OnSpinnerListener {
        void onItemSelected(String item, int indexItem);

        void onAddNewItemSuccess(String item, int indexItem);

        void onAddNewItemCancel();
    }
}
