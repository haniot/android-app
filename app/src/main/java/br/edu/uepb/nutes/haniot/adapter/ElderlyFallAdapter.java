package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.model.Fall;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the Elderly Falls.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyFallAdapter extends BaseAdapter<Fall> {
    private final String LOG = "ElderlyMonitoredAdapter";
    private final Context context;
    private String lastDate = "";

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public ElderlyFallAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_elderly_fall, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Fall> itemsList) {
        if (holder instanceof ViewHolder) {
            final Fall fall = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.date.setText(DateUtils.formatDate(fall.getRegistrationDate(),
                    context.getResources().getString(R.string.datetime_format2)));

            if (fall.getProfile().getTarget() != null) {
                if (fall.getProfile().getTarget().isFinalized()) {
                    h.statusClassification.setText(context.getResources().getString(R.string.fall_characterization_positive));
                } else {
                    h.statusClassification.setText(context.getResources().getString(R.string.fall_characterization_negative));
                }
            } else {
                h.statusClassification.setText(context.getResources().getString(R.string.fall_characterization_not_exist));
            }

            // set section
            String currentDate = DateUtils.formatDate(fall.getRegistrationDate(), "yyyy");
            if (!currentDate.equals(lastDate)) {
                if (DateUtils.isYear(fall.getRegistrationDate()))
                    h.titleSection.setText(context.getResources().getString(R.string.elderly_fall_registered_current_year));
                else
                    h.titleSection.setText(context.getResources()
                            .getString(R.string.elderly_fall_registered_in, currentDate));
                h.boxSection.setVisibility(View.VISIBLE);
                lastDate = currentDate;
            } else {
                h.boxSection.setVisibility(View.GONE);
            }

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (ElderlyFallAdapter.super.mListener != null)
                    ElderlyFallAdapter.super.mListener.onItemClick(fall);
            });

            // OnLongClick Item
            h.mView.setOnLongClickListener(v -> {
                if (ElderlyFallAdapter.super.mListener != null)
                    ElderlyFallAdapter.super.mListener.onLongItemClick(h.mView, fall);
                return false;
            });

            // call Animation function
            setAnimation(h.mView, position);
        }
    }


    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    /**
     * Class ViewHolder for item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.date_fall_textview)
        TextView date;

        @BindView(R.id.status_classification_fall_textView)
        TextView statusClassification;

        @Nullable
        @BindView(R.id.box_section)
        LinearLayout boxSection;

        @Nullable
        @BindView(R.id.section_title_textview)
        TextView titleSection;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            this.mView = view.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }
}

