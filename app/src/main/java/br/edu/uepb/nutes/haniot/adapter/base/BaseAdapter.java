package br.edu.uepb.nutes.haniot.adapter.base;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Base Recycler.ViewHolder.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */

/**
 * Class BaseAdapter for RecyclerView.Adapter.
 *
 * @param <T>
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    protected List<T> itemsList;
    protected int lastPosition = -1;
    public OnRecyclerViewListener mListener;

    protected BaseAdapter() {
        this.itemsList = new ArrayList<>();
    }

    /**
     * Get list items.
     *
     * @return List<T>
     */
    public List<T> getItems() {
        return itemsList;
    }

    /**
     * Checks if item list is empty
     *
     * @return boolean
     */
    public boolean itemsIsEmpty() {
        return itemsList.isEmpty();
    }

    /**
     * Get first item.
     *
     * @return T
     */
    public T getFirstItem() {
        if (!itemsIsEmpty()) return itemsList.get(0);
        return null;
    }

    /**
     * Add item and notify you that a new item has been entered.
     *
     * @param item T
     */
    public void addItem(T item) {
        if (item != null) {
            itemsList.add(item);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(itemsList.size() - 1);
                }
            });
        }
    }

    /**
     * Add all itemsList and notify data set changed.
     *
     * @param items List<T>
     */
    public void addItems(List<T> items) {
        if (items != null) {
            this.itemsList.addAll(items);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Remove item and notify you that item has been removed.
     *
     * @param item T
     */
    public void removeItem(T item) {
        if (item != null) {
            itemsList.remove(item);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Clear the list of itemsList and notifies you that the data set has changed.
     */
    public void clearItems() {
        itemsList.clear();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Remove current itemsList and add new ones and notify you that the dataset has changed.
     *
     * @param items List<T>
     */
    public void replace(List<T> items) {
        if (items != null && !items.isEmpty()) {
            items.clear();
            addItems(items);
        }
    }

    /**
     * Apply animation to list itemsList.
     *
     * @param view
     * @param position
     */
    public void setAnimation(View view, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            anim.setDuration(400);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    /**
     * Set OnRecyclerViewListener.
     *
     * @param mListener
     */
    public void setListener(OnRecyclerViewListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createView(parent, viewType);
        BaseRecyclerViewHolder holder = createViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        showData(holder, position, itemsList);
    }

    @Override
    public int getItemCount() {
        return itemsList != null ? itemsList.size() : 0;
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        clearAnimation(holder);
    }

    /**
     * Load the layout of the item.
     *
     * @param viewGroup ViewGroup
     * @param viewType  int
     * @return View
     */
    public abstract View createView(ViewGroup viewGroup, int viewType);

    /**
     * Create
     *
     * @param view View
     * @return BaseRecyclerViewHolder {@link BaseRecyclerViewHolder}
     */
    public abstract BaseRecyclerViewHolder createViewHolder(View view);

    /**
     * Constructs item for display.
     *
     * @param holder    {@link BaseRecyclerViewHolder}
     * @param position  int
     * @param itemsList List<T>
     */
    public abstract void showData(BaseRecyclerViewHolder holder, int position, List<T> itemsList);

    /**
     * Clear animation in view.
     *
     * @param holder {@link BaseRecyclerViewHolder}
     */
    public abstract void clearAnimation(BaseRecyclerViewHolder holder);
}
