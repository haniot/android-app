package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class EvaluationExpandableAdapter extends ExpandableRecyclerViewAdapter<EvaluationExpandableAdapter.HeaderViewHolder, EvaluationExpandableAdapter.ViewHolder> {
    private Context context;
    protected int lastPosition = -1;
    public OnClick<ItemEvaluation> mListener;

    public EvaluationExpandableAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
    }

    public void setListener(OnClick<ItemEvaluation> mListener) {
        this.mListener = mListener;
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
    public HeaderViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View itemView = mInflater.inflate(R.layout.item_evaluation_section, parent, false);

        return new HeaderViewHolder(itemView);
    }

    @Override
    public ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View itemView = mInflater.inflate(R.layout.item_evaluation, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindChildViewHolder(ViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        ItemEvaluation ig = (ItemEvaluation) group.getItems().get(childIndex);
        ViewHolder h = (ViewHolder) holder;

        h.box.setVisibility(View.VISIBLE);
        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);
        h.checkItem.setVisibility(View.VISIBLE);
        h.QuizText.setVisibility(View.VISIBLE);

        h.imageItem.setImageResource(ig.getIcon());
        h.textDescription.setText(ig.getTitle());
        h.textDate.setText(ig.getDate());
        h.texTime.setText(ig.getTime());
        if (ig.getType() == ItemEvaluation.TYPE_QUIZ) {

            h.textMeasurement.setVisibility(View.GONE);
            h.textMeasurementType.setVisibility(View.GONE);
            h.QuizText.setText(ig.getQuizText());

        } else if (ig.getType() == ItemEvaluation.TYPE_MEASUREMENT) {
            h.mView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(ig);
                }
            });
            h.textMeasurement.setVisibility(View.VISIBLE);
            h.textMeasurementType.setVisibility(View.VISIBLE);
            h.textMeasurement.setText(ig.getValueMeasurement());
            h.textMeasurementType.setText(ig.getUnitMeasurement());
            h.QuizText.setVisibility(View.GONE);

        } else if (ig.getType() == ItemEvaluation.TYPE_EMPTY) {
            h.mView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAddMeasurementClick(ig.getTitle(), ig.getTypeEvaluation());
                }
            });
            h.QuizText.setText(context.getResources().getString(R.string.evaluation_empty_message));
            h.box.setVisibility(GONE);
            h.checkItem.setChecked(ig.isChecked());
            h.checkItem.setVisibility(View.INVISIBLE);
            h.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ig.setChecked(!ig.isChecked());
                notifyDataSetChanged();
            });
        }

        setAnimation(h.mView, childIndex);
    }

    @Override
    public void onBindGroupViewHolder(HeaderViewHolder holder, int flatPosition, ExpandableGroup group) {

        HeaderViewHolder holder2 = (HeaderViewHolder) holder;
        holder2.categoryTitle.setText(group.getTitle());
    }

    public class ViewHolder extends ChildViewHolder {
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

    public class HeaderViewHolder extends GroupViewHolder {
        final View mView;

        @BindView(R.id.category_title)
        TextView categoryTitle;

        @BindView(R.id.expand)
        ImageView expand;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        @Override
        public void expand() {
            super.expand();
            expand.setRotation(180);
        }

        @Override
        public void collapse() {
            expand.setRotation(0);
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }

    public interface OnClick<I> extends OnRecyclerViewListener<ItemEvaluation> {

        void onAddMeasurementClick(String name, int type);
    }
}