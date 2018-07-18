package br.edu.uepb.nutes.haniot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the Elderlys.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyMonitoredAdapter extends BaseAdapter<Elderly> {
    private final String LOG = "ElderlyMonitoredAdapter";
    private final Context context;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public ElderlyMonitoredAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_elderly, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Elderly> itemsList) {
        if (holder instanceof ViewHolder) {
            final Elderly elderly = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(elderly.getName());
            h.fallRecords.setText(String.format(context.getResources()
                    .getString(R.string.elderly_fall_register), 3));

            // textview fall risk
            h.fallRisk.setVisibility(View.VISIBLE);
            h.fallRisk.setBackgroundResource(R.drawable.rounded_corner);
            switch (elderly.getFallRisk()) {
                case 1:
                    ViewCompat.setBackgroundTintList(h.fallRisk, ColorStateList.valueOf(
                            context.getResources().getColor(R.color.colorLightGreen)));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_low));
                    break;
                case 2:
                    ViewCompat.setBackgroundTintList(h.fallRisk, ColorStateList.valueOf(
                            context.getResources().getColor(R.color.colorAmber)));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_moderate));
                    break;
                case 3:
                    ViewCompat.setBackgroundTintList(h.fallRisk, ColorStateList.valueOf(
                            context.getResources().getColor(R.color.colorRed)));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_high));
                    break;
                default:
                    h.fallRisk.setVisibility(View.GONE);
                    break;
            }

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (ElderlyMonitoredAdapter.super.mListener != null)
                    ElderlyMonitoredAdapter.super.mListener.onItemClick(elderly);
            });

            // OnLongClick Item
            h.mView.setOnLongClickListener(v -> {
                if (ElderlyMonitoredAdapter.super.mListener != null)
                    ElderlyMonitoredAdapter.super.mListener.onLongItemClick(h.mView, elderly);
                return false;
            });

            // OnClick menu icon menu context
            h.menuContext.setOnClickListener(v -> {
                if (ElderlyMonitoredAdapter.super.mListener != null)
                    ElderlyMonitoredAdapter.super.mListener.onMenuContextClick(h.menuContext, elderly);
            });

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(elderly.getName());

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .buildRoundRect(elderly.getName().substring(0, 1), color, 10);

            h.image.setImageDrawable(drawable);

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

        @BindView(R.id.elderly_name_textview)
        TextView name;

        @BindView(R.id.elderly_imageView)
        ImageView image;

        @BindView(R.id.elderly_fall_records_textView)
        TextView fallRecords;

        @BindView(R.id.elderly_fall_risk_textView)
        TextView fallRisk;

        @Nullable
        @BindView(R.id.menu_context_list_elderly_imageButton)
        ImageButton menuContext;

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

