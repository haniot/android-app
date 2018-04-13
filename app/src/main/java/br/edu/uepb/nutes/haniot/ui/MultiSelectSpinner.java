package br.edu.uepb.nutes.haniot.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        dialogBuilder = new AlertDialog.Builder(context);
        mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        _items = new ArrayList<>();
        _itemsSelected = new ArrayList<>();

        super.setAdapter(mAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        Log.d("TETING", "which is value: " + which);
        if (_itemsSelected != null && which < _itemsSelected.size()) {
            _itemsSelected.set(which, isChecked);

            mAdapter.clear();
            String result = buildSelectedItemString();
            if (result.isEmpty()) build();
            else mAdapter.add(result);

            Log.d("TEST", this.isEmpty() + "");
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        if (_items.isEmpty())
            dialogBuilder.setMessage(messageDialogEmpty);

        String[] _arrayItems = _items.toArray(new String[_items.size()]);
        boolean[] _arraySelectItems = itemsSelectedToArray();
        dialogBuilder.setMultiChoiceItems(_arrayItems, _arraySelectItems, this);
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
        boolean[] result = new boolean[_itemsSelected.size()];
        Log.d("DDD", "RE " + Arrays.toString(_itemsSelected.toArray()));
        for (int i = 0; i < result.length; i++) {
            Log.d("DDD", "RE " + _itemsSelected.get(0));
            result[i] = _itemsSelected.get(i);
        }

        return result;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    /**
     * Add new item.
     *
     * @param item {@link String}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner item(String item) {
        if (item == null) throw new IllegalArgumentException("Item is null");

        _items.add(item);
        return this;
    }

    /**
     * Add items.
     *
     * @param items {@link List<String>}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner items(List<String> items) {
        _items = items;
        _itemsSelected = new ArrayList<>();
        Collections.fill(_itemsSelected, false);
        return this;
    }

    /**
     * Add items.
     *
     * @param items {@link String[]}
     * @return MultiSelectSpinner
     */
    public MultiSelectSpinner items(String[] items) {
        items(Arrays.asList(items));
        return this;
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
     * Get {@link List<String>} items selected.
     *
     * @return List {@link List<String>}
     */
    public List<String> getSelectedStrings() {
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
    public List<Integer> getSelectedIndicies() {
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
}
