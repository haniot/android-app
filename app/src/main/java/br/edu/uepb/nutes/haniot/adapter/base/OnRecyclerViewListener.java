package br.edu.uepb.nutes.haniot.adapter.base;

import android.view.MenuItem;
import android.view.View;

/**
 * Interface to capture event click on itemsList listed by the adapter.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface OnRecyclerViewListener<T> {
    void onItemClick(T item);

    void onLongItemClick(View v, T item);

    void onMenuContextClick(View v, T item);
}
