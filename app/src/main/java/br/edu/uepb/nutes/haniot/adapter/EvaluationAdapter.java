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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.ChronicDiseaseType;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeendingHabitsRecordType;
import br.edu.uepb.nutes.haniot.data.model.FoodType;
import br.edu.uepb.nutes.haniot.data.model.FrequencyAnswersType;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SchoolActivityFrequencyType;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;

public class EvaluationAdapter extends ExpandableRecyclerViewAdapter<EvaluationAdapter.HeaderViewHolder,
        EvaluationAdapter.ViewHolder> {
    private Context context;
    private int lastPosition = -1;
    private DecimalFormat decimalFormat;
    public OnClick mListener;

    public EvaluationAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
        decimalFormat = new DecimalFormat(context.getString(R.string.format_number2),
                new DecimalFormatSymbols(Locale.US));
    }

    public void setListener(OnClick mListener) {
        this.mListener = mListener;
    }

    /**
     * Expand all groups.
     */
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

    @Override
    public void onBindChildViewHolder(ViewHolder h, int flatPosition, ExpandableGroup group, int childIndex) {
        ItemEvaluation ig = (ItemEvaluation) group.getItems().get(childIndex);
        resetView(h, ig);

        if (ig.getTypeHeader() == ItemEvaluation.TYPE_LOADING) {
            createLoadingView(h, ig);
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_QUIZ) {
            createQuizView(h, ig, group);
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

    /**
     * Restore view to default.
     *
     * @param h
     * @param ig
     */
    private void resetView(ViewHolder h, ItemEvaluation ig) {
        h.checkItem.setChecked(ig.isChecked());
        h.texTime.setVisibility(View.VISIBLE);
        h.timeIcon.setVisibility(View.VISIBLE);
        h.itemQuizView.setVisibility(GONE);
        h.boxOthersInfo.setVisibility(View.VISIBLE);
        h.boxMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);
        h.checkItem.setVisibility(View.VISIBLE);
        h.messageText.setVisibility(View.VISIBLE);
        h.warning.setVisibility(View.INVISIBLE);
        h.loading.setVisibility(View.GONE);
        h.imageItem.setImageResource(ig.getIcon());
        h.textDescription.setText(ig.getTitle());
        h.textMax.setVisibility(GONE);
        h.textMaxType.setVisibility(GONE);
        h.textMin.setVisibility(GONE);
        h.textMinType.setVisibility(GONE);
        h.textMinType.setTextSize(16);
        h.textMaxType.setTextSize(16);
        h.textMeasurementType.setTextSize(16);
    }

    /**
     * View displayed when there is no data.
     *
     * @param h
     * @param ig
     */
    private void createErrorView(ViewHolder h, ItemEvaluation ig) {
        h.warning.setVisibility(View.VISIBLE);
        h.messageText.setVisibility(View.VISIBLE);
        h.messageText.setText(context.getResources().getString(R.string.evaluation_error_message));
        h.boxOthersInfo.setVisibility(GONE);
        h.boxMeasurement.setVisibility(GONE);
        h.checkItem.setVisibility(View.INVISIBLE);
//        h.mView.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onRefreshClick(ig.getTitle(), ig.getTypeEvaluation());
//            }
//        });
    }

    private void createEmptyView(ViewHolder h, ItemEvaluation ig) {
        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation());
            }
        });
        h.messageText.setVisibility(View.VISIBLE);
        h.messageText.setText(context.getResources().getString(R.string.evaluation_empty_message));
        h.boxOthersInfo.setVisibility(GONE);
        h.boxMeasurement.setVisibility(GONE);
        h.checkItem.setVisibility(View.INVISIBLE);
    }

    /**
     * View responsible for displaying measurement data.
     *
     * @param h
     * @param ig
     */
    private void createMeasurementView(ViewHolder h, ItemEvaluation ig) {
        h.checkItem.setChecked(ig.isChecked());
        h.mView.setOnClickListener(v -> {
            ig.setChecked(!ig.isChecked());
            h.checkItem.setChecked(!h.checkItem.isChecked());
            mListener.onSelectClick(ig, ig.isChecked());
        });

        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);

        int type = ig.getTypeEvaluation();
        Measurement measurement = ig.getMeasurement();
        h.textMeasurementType.setText(measurement.getUnit());

        if (type == TypeEvaluation.HEARTRATE) {
            h.textMax.setVisibility(View.VISIBLE);
            h.textMaxType.setVisibility(View.VISIBLE);
            h.textMin.setVisibility(View.VISIBLE);
            h.textMinType.setVisibility(View.VISIBLE);
            h.textMinType.setTextSize(11);
            h.textMaxType.setTextSize(11);
            h.textMeasurementType.setTextSize(12);

            double average = 0.0;
            HeartRateItem min = measurement.getDataset().get(0);
            HeartRateItem max = measurement.getDataset().get(0);

            for (HeartRateItem heartRateItem : measurement.getDataset()) {
                if (heartRateItem.getValue() <= min.getValue()) min = heartRateItem;
                if (heartRateItem.getValue() >= max.getValue()) max = heartRateItem;
                average += heartRateItem.getValue();
            }
            average = average / measurement.getDataset().size();
            h.textMeasurement.setText(String.valueOf((int) average));
            h.textMeasurementType.setText(String.format("%s (Média)", measurement.getUnit()));
            h.textMin.setText(String.valueOf(min.getValue()));
            h.textMinType.setText(String.format("%s (Min)", measurement.getUnit()));
            h.textMax.setText(String.valueOf(max.getValue()));
            h.textMaxType.setText(String.format("%s (Max)", measurement.getUnit()));
            h.texTime.setText(String.format("%s - %s",
                    DateUtils.convertDateTimeUTCToLocale(min.getTimestamp(),
                            context.getString(R.string.time_format_simple)),
                    DateUtils.convertDateTimeUTCToLocale(max.getTimestamp(),
                            context.getString(R.string.time_format_simple))));
            h.textDate.setText(DateUtils
                    .convertDateTimeUTCToLocale(min.getTimestamp(),
                            context.getString(R.string.date_format)));
        } else {
            h.textDate.setText(DateUtils
                    .convertDateTimeUTCToLocale(measurement.getTimestamp(),
                            context.getString(R.string.date_format)));
            h.texTime.setText(DateUtils
                    .convertDateTimeUTCToLocale(measurement.getTimestamp(),
                            context.getString(R.string.time_format_simple)));
            if (type == TypeEvaluation.TEMPERATURE
                    || type == TypeEvaluation.WEIGHT
                    || type == TypeEvaluation.FAT) {
                h.textMeasurement.setText(decimalFormat.format(measurement.getValue()));
            } else if (type == TypeEvaluation.WAIST_CIRCUMFERENCE
                    || type == TypeEvaluation.HEIGHT) {
                h.textMeasurement.setText(String.valueOf((int) measurement.getValue()));
            } else if (type == TypeEvaluation.BLOOD_PRESSURE) {
                h.textMeasurement.setText(String.format(Locale.getDefault(), "%d/%d",
                        measurement.getSystolic(), measurement.getDiastolic()));
            }
        }
        h.messageText.setVisibility(View.GONE);
    }

    /**
     * View responsible for displaying questionnaire data.
     *
     * @param h
     * @param ig
     * @param group
     */
    private void createQuizView(ViewHolder h, ItemEvaluation ig, ExpandableGroup group) {
        h.itemQuizView.clear();
        h.textMeasurement.setVisibility(View.GONE);
        h.textMeasurementType.setVisibility(View.GONE);
        h.messageText.setVisibility(View.VISIBLE);
        h.boxOthersInfo.setVisibility(GONE);
        h.boxMeasurement.setVisibility(GONE);
        h.checkItem.setChecked(ig.isChecked());

        h.mView.setOnClickListener(v -> {
            for (ItemEvaluation itemEvaluation : (List<ItemEvaluation>) group.getItems()) {
                itemEvaluation.setChecked(false);
            }
            notifyDataSetChanged();

            ig.setChecked(!ig.isChecked());
            h.checkItem.setChecked(!h.checkItem.isChecked());
            mListener.onSelectClick(ig, ig.isChecked());
        });

        StringBuilder stringBuilder = new StringBuilder();
        String date = "";
        String time = "";

        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS:
                SleepHabit sleepHabit = ig.getSleepHabit();
                date = DateUtils.convertDateTimeUTCToLocale(sleepHabit.getCreatedAt(),
                        context.getString(R.string.date_format));
                time = DateUtils.convertDateTimeUTCToLocale(sleepHabit.getCreatedAt(),
                        context.getString(R.string.time_format_simple));
                stringBuilder.append("\nDorme às ").append(sleepHabit.getWeekDaySleep())
                        .append(" horas");
                stringBuilder.append("\nAcorda às ").append(sleepHabit.getWeekDayWakeUp())
                        .append(" horas");
                break;
            case MEDICAL_RECORDS:
                MedicalRecord medicalRecord = ig.getMedicalRecord();
                if (medicalRecord == null) return;
                date = DateUtils.convertDateTimeUTCToLocale(medicalRecord.getCreatedAt(),
                        context.getString(R.string.date_format));
                time = DateUtils.convertDateTimeUTCToLocale(medicalRecord.getCreatedAt(),
                        context.getString(R.string.time_format_simple));
                if (medicalRecord.getChronicDiseases() != null) {
                    for (ChronicDisease chronicDisease : medicalRecord.getChronicDiseases())
                        stringBuilder
                                .append(ChronicDiseaseType.ChronicDisease
                                        .getString_PTBR(chronicDisease.getType()))
                                .append(": ")
                                .append(ChronicDiseaseType.DisieaseHistory
                                        .getStringPTBR(chronicDisease.getDiseaseHistory()))
                                .append("\n");
                }
                break;
            case FEEDING_HABITS:
                FeedingHabitsRecord feedingHabitsRecord = ig.getFeedingHabitsRecord();
                date = DateUtils.convertDateTimeUTCToLocale(feedingHabitsRecord.getCreatedAt(),
                        context.getString(R.string.date_format));
                time = DateUtils.convertDateTimeUTCToLocale(feedingHabitsRecord.getCreatedAt(),
                        context.getString(R.string.time_format_simple));
                stringBuilder.append("\nCopos de água por dia: ").append(FrequencyAnswersType.Frequency
                        .getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("\nCafé da manhã: ").append(FrequencyAnswersType.Frequency
                        .getStringPTBR(feedingHabitsRecord.getBreakfastDailyFrequency()));
                for (WeeklyFoodRecord weeklyFoodRecord : feedingHabitsRecord.getWeeklyFeedingHabits())
                    stringBuilder
                            .append("\n")
                            .append(FoodType.getStringPTBR(weeklyFoodRecord.getFood()))
                            .append(": ")
                            .append(FeendingHabitsRecordType.SevenDaysFeedingFrequency
                                    .getStringPTBR(weeklyFoodRecord.getSevenDaysFreq()));
                break;
            case PHYSICAL_ACTIVITY:
                PhysicalActivityHabit physicalActivityHabit = ig.getPhysicalActivityHabit();
                date = DateUtils.convertDateTimeUTCToLocale(physicalActivityHabit.getCreatedAt(),
                        context.getString(R.string.date_format));
                time = DateUtils.convertDateTimeUTCToLocale(physicalActivityHabit.getCreatedAt(),
                        context.getString(R.string.time_format_simple));
                stringBuilder.append("\nEsportes praticados durante a semana: \n");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append(sport).append("\n");
                }
                stringBuilder.append("\nFrequência de atividades físicas na escola: \n")
                        .append(SchoolActivityFrequencyType
                                .getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
        }
        h.messageText.setText(stringBuilder.append(String.format("\n\nRespondido em %s às %s", date, time)));
    }

    /**
     * View displayed while downloading the data.
     *
     * @param h
     * @param ig
     */
    private void createLoadingView(ViewHolder h, ItemEvaluation ig) {
        h.loading.setVisibility(View.VISIBLE);
        h.boxOthersInfo.setVisibility(GONE);
        h.boxMeasurement.setVisibility(GONE);
        h.messageText.setVisibility(GONE);
        h.checkItem.setVisibility(GONE);
    }

    @Override
    public void onBindGroupViewHolder(HeaderViewHolder holder, int flatPosition, ExpandableGroup
            group) {
        holder.categoryTitle.setText(group.getTitle());
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
        @BindView(R.id.textMin)
        TextView textMin;
        @BindView(R.id.textMinType)
        TextView textMinType;
        @BindView(R.id.textMax)
        TextView textMax;
        @BindView(R.id.textMaxType)
        TextView textMaxType;
        @BindView(R.id.text_time_measurement)
        TextView texTime;
        @BindView(R.id.quiz_text)
        TextView messageText;
        @BindView(R.id.text_date_measurement)
        TextView textDate;
        @BindView(R.id.check_item)
        CheckBox checkItem;
        @BindView(R.id.box_other_info)
        LinearLayout boxOthersInfo;
        @BindView(R.id.box_measurement)
        LinearLayout boxMeasurement;
        @BindView(R.id.warning)
        ImageView warning;
        @BindView(R.id.loading)
        ProgressBar loading;
        @BindView(R.id.time_measuerement)
        ImageView timeIcon;
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

        void onSelectClick(ItemEvaluation itemEvaluation, boolean selected);

    }
}