package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.ChronicDiseaseType;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeendingHabitsRecordType;
import br.edu.uepb.nutes.haniot.data.model.FoodType;
import br.edu.uepb.nutes.haniot.data.model.FrequencyAnswersType;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SchoolActivityFrequencyType;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;

public class EvaluationExpandableAdapter extends ExpandableRecyclerViewAdapter<EvaluationExpandableAdapter.HeaderViewHolder, EvaluationExpandableAdapter.ViewHolder> {
    private Context context;
    protected int lastPosition = -1;
    public OnClick mListener;

    public EvaluationExpandableAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
    }

    public void setListener(OnClick mListener) {
        this.mListener = mListener;
    }

    public void expandAll() {
        for (int i = 0; i < getItemCount(); i++) {
            if (!isGroupExpanded(i)) {
                toggleGroup(i);
            }
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

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public void onBindChildViewHolder(ViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        ItemEvaluation ig = (ItemEvaluation) group.getItems().get(childIndex);
        ViewHolder h = (ViewHolder) holder;

        h.itemQuizView.setVisibility(GONE);
        h.box.setVisibility(View.VISIBLE);
        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);
        h.checkItem.setVisibility(View.VISIBLE);
        h.QuizText.setVisibility(View.VISIBLE);
        h.warning.setVisibility(View.INVISIBLE);
        h.loading.setVisibility(View.GONE);
        h.imageItem.setImageResource(ig.getIcon());
        h.textDescription.setText(ig.getTitle());
        h.textDate.setText(ig.getDate());
        h.texTime.setText(ig.getTime());

        h.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ig.setChecked(!ig.isChecked());
            notifyDataSetChanged();
        });

        if (ig.getTypeHeader() == ItemEvaluation.TYPE_LOADING) {
            createLoadingView(h, ig);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_QUIZ) {
            createQuizView(h, ig);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_MEASUREMENT) {
            createMeasurementView(h, ig);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_EMPTY) {
            createEmptyView(h, ig);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_EMPTY_REQUIRED) {
            createEmptyView(h, ig);
            h.warning.setVisibility(View.VISIBLE);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_ERROR) {
            createErrorView(h, ig);
        }
        setAnimation(h.mView, childIndex);
    }

    private void createErrorView(ViewHolder h, ItemEvaluation ig) {
        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onRefreshClick(ig.getTitle(), ig.getTypeEvaluation());
            }
        });
        h.QuizText.setVisibility(View.VISIBLE);
        h.QuizText.setText(context.getResources().getString(R.string.evaluation_error_message));
        h.box.setVisibility(GONE);
        h.checkItem.setChecked(ig.isChecked());
        h.checkItem.setVisibility(View.INVISIBLE);
        h.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ig.setChecked(!ig.isChecked());
            notifyDataSetChanged();
        });
    }

    private void createEmptyView(ViewHolder h, ItemEvaluation ig) {

        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation());
            }
        });
        h.QuizText.setVisibility(View.VISIBLE);
        h.QuizText.setText(context.getResources().getString(R.string.evaluation_empty_message));
        h.box.setVisibility(GONE);
        h.checkItem.setChecked(ig.isChecked());
        h.checkItem.setVisibility(View.INVISIBLE);
    }

    private void createMeasurementView(ViewHolder h, ItemEvaluation ig) {

        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSelectClick(ig);
            }
        });
        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);
        h.textMeasurement.setText(ig.getValueMeasurement());
        h.textMeasurementType.setText(ig.getUnitMeasurement());
        h.QuizText.setVisibility(View.GONE);
    }

    private void createQuizView(ViewHolder h, ItemEvaluation ig) {
        h.itemQuizView.clear();
        h.textMeasurement.setVisibility(View.GONE);
        h.textMeasurementType.setVisibility(View.GONE);
        h.QuizText.setVisibility(View.VISIBLE);
        h.box.setVisibility(GONE);

        //TODO TESTE

        String a = "";
        for (int i = 0; i <= 6; i++) {
            a += "Refrigerante: Todos os dias\n";
        }

        StringBuilder stringBuilder = new StringBuilder();
        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS:
                SleepHabit sleepHabit = ig.getSleepHabit();
                stringBuilder.append("\nDorme às ").append(sleepHabit.getWeekDaySleep()).append(" horas");
                stringBuilder.append("\nAcorda às ").append(sleepHabit.getWeekDayWakeUp()).append(" horas");
                break;
            case MEDICAL_RECORDS:
                MedicalRecord medicalRecord = ig.getMedicalRecord();
                for (ChronicDisease chronicDisease : medicalRecord.getChronicDiseases())
                    stringBuilder
                            .append(ChronicDiseaseType.ChronicDisease.getString_PTBR(chronicDisease.getType()))
                            .append(": ")
                            .append(ChronicDiseaseType.DisieaseHistory.getStringPTBR(chronicDisease.getDiseaseHistory()))
                            .append("\n");
                break;
            case FEEDING_HABITS:
                FeedingHabitsRecord feedingHabitsRecord = ig.getFeedingHabitsRecord();
                stringBuilder.append("\nCopos de água por dia: ").append(FoodType.getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("\nCafé da manhã: ").append(FrequencyAnswersType.Frequency.getStringPTBR(feedingHabitsRecord.getBreakfastDailyFrequency()));
                for (WeeklyFoodRecord weeklyFoodRecord : feedingHabitsRecord.getWeeklyFeedingHabits())
                    stringBuilder
                            .append("\n")
                            .append(FoodType.getStringPTBR(weeklyFoodRecord.getFood()))
                            .append(": ")
                            .append(FeendingHabitsRecordType.SevenDaysFeedingFrequency.getStringPTBR(weeklyFoodRecord.getSevenDaysFreq()));
                break;
            case PHYSICAL_ACTIVITY:
                PhysicalActivityHabit physicalActivityHabit = ig.getPhysicalActivityHabit();
                stringBuilder.append("\nEsportes praticados durante a semana: \n");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append(sport).append(",\n");
                }
                stringBuilder.append("\nFrequência de atividades físicas na escola: \n")
                        .append(SchoolActivityFrequencyType.getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
        }

        h.QuizText.setText(stringBuilder.append("\n\nRespondido em 23/02/2019 às 13:00").toString());
        //
    }

    private void createLoadingView(ViewHolder h, ItemEvaluation ig) {
        h.loading.setVisibility(View.VISIBLE);
        h.box.setVisibility(GONE);
        h.QuizText.setVisibility(GONE);
        h.checkItem.setVisibility(GONE);
    }

    @Override
    public void onBindGroupViewHolder(HeaderViewHolder holder, int flatPosition, ExpandableGroup
            group) {
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
        @BindView(R.id.warning)
        ImageView warning;
        @BindView(R.id.loading)
        ProgressBar loading;

        @BindView(R.id.item_quiz)
        ItemQuizView itemQuizView;

        @BindView(R.id.box_quiz)
        LinearLayout boxQuiz;

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

    public interface OnClick {

        void onAddItemClick(String name, int type);

        void onRefreshClick(String name, int type);

        void onSelectClick(ItemEvaluation itemEvaluation);

    }
}