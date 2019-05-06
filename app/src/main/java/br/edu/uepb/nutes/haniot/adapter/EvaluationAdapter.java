package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class EvaluationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ItemEvaluation> itemEvaluations;
    protected int lastPosition = -1;
    public OnRecyclerViewListener mListener;

    public EvaluationAdapter(Context context) {
        this.context = context;
        this.itemEvaluations = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (viewType == ItemEvaluation.TYPE_EMPTY) {
            View itemView = mInflater.inflate(R.layout.item_evaluation_section, parent, false);
            return new HeaderViewHolder(itemView);
        } else {
            View itemView = mInflater.inflate(R.layout.item_evaluation, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public void setListener(OnRecyclerViewListener mListener) {
        this.mListener = mListener;
    }

    public void setmListener(OnRecyclerViewListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (type == ItemEvaluation.TYPE_EMPTY) {
            ItemEvaluation header = itemEvaluations.get(i);
            HeaderViewHolder holder2 = (HeaderViewHolder) viewHolder;
            holder2.categoryTitle.setText(header.getTitle());
            // your logic here
        } else {
            ItemEvaluation ig = (ItemEvaluation) itemEvaluations.get(i);
            ViewHolder h = (ViewHolder) viewHolder;
            // your logic here

            h.imageItem.setImageResource(ig.getIcon());
            h.textDescription.setText(ig.getTitle());
            h.textDate.setText(ig.getDate());
            h.texTime.setText(ig.getTime());
            if (ig.getType() == ItemEvaluation.TYPE_QUIZ) {

                h.textMeasurement.setVisibility(View.GONE);
                h.textMeasurementType.setVisibility(View.GONE);
                h.QuizText.setText(ig.getQuizText());

            } else if (ig.getType() == ItemEvaluation.TYPE_MEASUREMENT) {

                h.textMeasurement.setVisibility(View.VISIBLE);
                h.textMeasurementType.setVisibility(View.VISIBLE);
                h.textMeasurement.setText(ig.getValueMeasurement());
                h.textMeasurementType.setText(ig.getUnitMeasurement());
                h.QuizText.setVisibility(View.GONE);

            } else if (ig.getType() == ItemEvaluation.TYPE_EMPTY) {

                h.QuizText.setText(ig.getQuizText());
                h.box.setVisibility(GONE);
                h.checkItem.setVisibility(View.INVISIBLE);

            }

            h.mView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(ig);
                }
            });

            setAnimation(h.mView, i);
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

    @Override
    public int getItemViewType(int position) {
        return itemEvaluations.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return itemEvaluations.size();
    }

    /**
     * Add all itemsList and notify data set changed.
     *
     * @param items List<T>
     */
    public void addItems(List<ItemEvaluation> items) {
        if (items != null) {
            this.itemEvaluations.addAll(items);

            new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.imageItem)
        ImageView imageItem;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView(R.id.textMeasurement)
        TextView textMeasurement;
        @BindView(R.id.textMeasurementType)
        TextView textMeasurementType;
        @BindView(R.id.text_time_measurement)
        TextView texTime;
        @BindView(R.id.quiz_text)
        TextView QuizText;
        @BindView(R.id.text_date_measurement)
        TextView textDate;
        @BindView(R.id.check_item)
        CheckBox checkItem;
        @BindView(R.id.box_measurement)
        LinearLayout box;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.category_title)
        TextView categoryTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }
}