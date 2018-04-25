package br.edu.uepb.nutes.haniot.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * UI element multi select values spinner.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MultiSelectSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {
    private final String TAG = "MultiSelectSpinner";

    private List<String> _items = null;
    private List<Boolean> _itemsSelected = null;
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private ArrayAdapter<String> mAdapter;
    private String messageDialogEmpty = "";
    private String hintMessage = null;
    private String title = null;

    /**
     * Constructor for use when instantiating directly.
     *
     * @param context
     */
    public MultiSelectSpinner(Context context) {
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
    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init() {
        Log.d(TAG, "init() multi spinner");
        dialogBuilder = new AlertDialog.Builder(context);
        mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        _items = new ArrayList<>();
        _itemsSelected = new ArrayList<>();

        super.setAdapter(mAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (_itemsSelected != null && which < _itemsSelected.size()) {
            _itemsSelected.set(which, isChecked);

            String result = buildSelectedItemString();
            if (result.isEmpty()) {
                build();
            } else {
                mAdapter.clear();
                mAdapter.add(result);
            }
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        if (this._items.isEmpty())
            dialogBuilder.setMessage(messageDialogEmpty);

        String[] _arrayItems = itemsToArray();
        boolean[] _arraySelectedItems = itemsSelectedToArray();
        dialogBuilder.setMultiChoiceItems(_arrayItems, _arraySelectedItems, this);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
        return true;
    }

    /**
     * Items selected List to Array.
     *
     * @return boolean[]
     */
    private boolean[] itemsSelectedToArray() {
        boolean[] result = new boolean[this._itemsSelected.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = this._itemsSelected.get(i);

        return result;
    }

    /**
     * Convert {@link List<String>} in [].
     *
     * @return String
     */
    private String[] itemsToArray() {
        String[] result = new String[this._items.size()];
        for (int i = 0; i < this._items.size(); i++)
            result[i] = this._items.get(i);

        return result;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    /**
     * Add items.
     *
     * @param items {@link List<String>}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner items(List<String> items) {
        // Remove string invalid
        items.remove(new String());
        if (items == null || items.isEmpty())
            return this;

        _items = items;
        _itemsSelected = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++)
            _itemsSelected.add(false);

        return this;
    }

    /**
     * Add items.
     *
     * @param items {@link String[]}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner items(String[] items) {
        List<String> result = new ArrayList<>(items.length);
        for (String s : items) result.add(s);

        items(result);
        return this;
    }

    /**
     * Add new item Dynamically.
     *
     * @param item {@link String}
     */
    public void addItem(String item, boolean isSelected) {
        if (item == null) throw new IllegalArgumentException("Item is null");
        this._items.add(item);
        this._itemsSelected.add(isSelected);

        String result = buildSelectedItemString();
        if (isSelected && !result.isEmpty()) {
            mAdapter.clear();
            mAdapter.add(result);
        }
    }

    /**
     * Add message hint.
     *
     * @param hintMessage {@link String}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner hint(@Nullable String hintMessage) {
        this.hintMessage = hintMessage;
        return this;
    }

    /**
     * Add message empty dialog.
     *
     * @param message {@link String}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner messageEmpty(String message) {
        this.messageDialogEmpty = message;
        return this;
    }

    /**
     * Add title in dialog.
     *
     * @param title {@link String}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner title(String title) {
        this.title = title;
        return this;
    }

    public void build() {
        mAdapter.clear();
        if (hintMessage != null) mAdapter.add(hintMessage);

        if (title != null) dialogBuilder.setTitle(title);
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

    /**
     * Select item in list.
     *
     * @param index
     */
    public void selection(int index) {
        for (int i = 0; i < _itemsSelected.size(); i++)
            _itemsSelected.set(i, false);

        if (index >= 0 && index < _itemsSelected.size()) _itemsSelected.set(index, true);
        else throw new IllegalArgumentException("Index index is out of bounds.");

        mAdapter.clear();
        mAdapter.add(buildSelectedItemString());
    }

    /**
     * Select array items in list.
     *
     * @param selectedIndicies
     */
    public void selection(int[] selectedIndicies) {
        for (int i = 0; i < _itemsSelected.size(); i++)
            _itemsSelected.set(i, false);

        for (int index : selectedIndicies) {
            if (index >= 0 && index < _itemsSelected.size()) _itemsSelected.set(index, true);
            else throw new IllegalArgumentException("Index index is out of bounds.");
        }

        mAdapter.clear();
        mAdapter.add(buildSelectedItemString());
    }

    /**
     * Selects items according to values from list passed as parameter
     *
     * @param selection {@link List<String>}
     */
    public void selection(List<String> selection) {
        for (int i = 0; i < _itemsSelected.size(); i++)
            _itemsSelected.set(i, false);

        for (String sel : selection) {
            for (int j = 0; j < _items.size(); ++j) {
                if (_items.get(j).equals(sel))
                    _itemsSelected.set(j, true);
            }
        }
        mAdapter.clear();
        mAdapter.add(buildSelectedItemString());
    }

    /**
     * Get {@link List<String>} items selected.
     *
     * @return List {@link List<String>}
     */
    public List<String> getSelectedItems() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.size(); ++i)
            if (_itemsSelected.get(i)) selection.add(_items.get(i));

        return selection;
    }

    /**
     * Get indexes of selected items.
     *
     * @return {@link List<Integer>}
     */
    public List<Integer> getIndexSelectedItems() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _items.size(); ++i)
            if (_itemsSelected.get(i)) selection.add(i);

        return selection;
    }

    /**
     * Build string items selected.
     *
     * @return {@link String}
     */
    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.size(); ++i) {
            if (_itemsSelected.get(i)) {
                if (foundOne) sb.append(", ");

                foundOne = true;
                sb.append(_items.get(i));
            }
        }
        return sb.toString();
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
            if (valueItem.isEmpty()) {
                callback.onCancel();
            } else {
                addItem(valueItem, true);
                callback.onSuccess(valueItem);
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
     * Interface callback Dialog add new item.
     */
    public interface OnItemAddCallback {
        void onSuccess(String item);

        void onCancel();
    }
}
