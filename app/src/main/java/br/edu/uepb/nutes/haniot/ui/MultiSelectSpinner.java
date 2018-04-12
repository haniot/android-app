package br.edu.uepb.nutes.haniot.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean[] mSelection = null;
    private Context context;
    AlertDialog.Builder dialogBuilder;

    ArrayAdapter<String> mAdapter;

    /**
     * Constructor for use when instantiating directly.
     *
     * @param context
     */
    public MultiSelectSpinner(Context context) {
        super(context);
        this.context = context;

        mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        dialogBuilder = new AlertDialog.Builder(getContext());
        super.setAdapter(mAdapter);
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

        mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            mAdapter.clear();
            mAdapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {

        if (_items == null) {
            _items = new ArrayList<>();
            dialogBuilder.setMessage("Ops teste");
        }
        String[] _array = _items.toArray(new String[_items.size()]);
        dialogBuilder.setMultiChoiceItems(_array, mSelection, this);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        setItems(Arrays.asList(items));
    }

    public void addItem(String item) {
        if (item == null) throw new IllegalArgumentException("Item is null");

        if (_items == null)
            _items = new ArrayList<>();
        _items.add(item);
    }

    public void setItems(List<String> items) {
        _items = items;
        mSelection = new boolean[_items.size()];
        mAdapter.clear();
        Arrays.fill(mSelection, false);
    }

    public void setSelectionEmpty(@Nullable String strEmpty) {
        mAdapter.clear();
        if (strEmpty != null) mAdapter.add(strEmpty);
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        mAdapter.clear();
        mAdapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        mAdapter.clear();
        mAdapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.size(); ++i) {
            if (mSelection[i]) {
                selection.add(_items.get(i));
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.size(); ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.size(); ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items.get(i));
            }
        }
        return sb.toString();
    }

}
