package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to getAllByUserId the Pilots Studies.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PilotStudyAdapter extends BaseAdapter<PilotStudy> {
    private final String LOG = "PilotStudyAdapter";
    private final Context context;

    /**
     * Constructor.
     *
     * @param context {@link Context}
     */
    public PilotStudyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_pilot_study, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<PilotStudy> itemsList) {
        if (holder instanceof ViewHolder) {
            final PilotStudy pilot = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(pilot.getName());
            h.dateStart.setText(DateUtils.convertDateTimeUTCToLocale(pilot.getStart(),
                    context.getResources().getString(R.string.date_format)));

            if (pilot.getEnd() != null && !pilot.getEnd().isEmpty()) {
                h.dateEnd.setText(DateUtils.convertDateTimeUTCToLocale(pilot.getEnd(),
                        context.getResources().getString(R.string.date_format)));
            }

            if (pilot.isActive()) {
                h.status.setText(context.getResources().getText(R.string.active_title));
                ViewCompat.setBackgroundTintList(h.status, ColorStateList.valueOf(
                        context.getResources().getColor(R.color.colorGreen)));
            } else {
                h.status.setText(context.getResources().getText(R.string.inactive_title));
                ViewCompat.setBackgroundTintList(h.status, ColorStateList.valueOf(
                        context.getResources().getColor(R.color.colorAmber)));
            }

            h.selected.setChecked(pilot.isSelected());
            if (pilot.isSelected()) {
                h.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.colorCardActive)
                );
            }

            // Click events
            h.mView.setOnClickListener(v -> {
                if (super.mListener != null) super.mListener.onItemClick(pilot);
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

        @BindView(R.id.card_view)
        CardView cardView;

        @BindView(R.id.pilot_name_textview)
        TextView name;

        @BindView(R.id.pilot_date_start_textView)
        TextView dateStart;

        @BindView(R.id.pilot_date_end_textView)
        TextView dateEnd;

        @BindView(R.id.pilot_status_textView)
        TextView status;

        @BindView(R.id.pilot_checkbox)
        AppCompatCheckBox selected;

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

