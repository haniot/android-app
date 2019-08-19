package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import br.edu.uepb.nutes.haniot.data.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeendingHabitsRecordType;
import br.edu.uepb.nutes.haniot.data.model.FoodType;
import br.edu.uepb.nutes.haniot.data.model.FrequencyAnswersType;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SchoolActivityFrequencyType;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.SociodemographicType;
import br.edu.uepb.nutes.haniot.data.model.SportsType;
import br.edu.uepb.nutes.haniot.data.model.ToothLesion;
import br.edu.uepb.nutes.haniot.data.model.ToothLesionType;
import br.edu.uepb.nutes.haniot.data.model.TypeEvaluation;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FAMILY_COHESION;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.FEEDING_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.ORAL_HEALTH;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.PHYSICAL_ACTIVITY;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SLEEP_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.TypeEvaluation.SOCIODEMOGRAPHICS;

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
        h.texTime.setVisibility(View.VISIBLE);
        h.timeIcon.setVisibility(View.VISIBLE);
        h.itemQuizView.setVisibility(GONE);
        h.boxOthersInfo.setVisibility(View.VISIBLE);
        h.boxMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurement.setVisibility(View.VISIBLE);
        h.textMeasurementType.setVisibility(View.VISIBLE);
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
    }

    /**
     * View responsible for displaying measurement data.
     *
     * @param h
     * @param ig
     */
    private void createMeasurementView(ViewHolder h, ItemEvaluation ig) {
        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation());
            }
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
            Log.w("AAA", "Errorrrrr " + measurement);
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

    private String getString(int id) {
        String string = context.getResources().getString(id);
        return string;
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

        StringBuilder stringBuilder = new StringBuilder();
        String date = "";
        String time = "";

        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS:
                SleepHabit sleepHabit = ig.getSleepHabit();
                stringBuilder.append("<h4>" + getString(R.string.sleep_at) + " </h4><p>").append(sleepHabit.getWeekDaySleep())
                        .append(getString(R.string.hours));
                stringBuilder.append("<h4>" + getString(R.string.wakeup_at) + " </h4><p>").append(sleepHabit.getWeekDayWakeUp())
                        .append(getString(R.string.hours));
                break;
            case MEDICAL_RECORDS:
                MedicalRecord medicalRecord = ig.getMedicalRecord();
                if (medicalRecord == null) return;
                if (medicalRecord.getChronicDiseases() != null) {
                    for (ChronicDisease chronicDisease : medicalRecord.getChronicDiseases())
                        stringBuilder
                                .append("<h4>" + ChronicDiseaseType.ChronicDisease
                                        .getString_PTBR(chronicDisease.getType()))
                                .append("</h4><p>")
                                .append(ChronicDiseaseType.DisieaseHistory
                                        .getStringPTBR(chronicDisease.getDiseaseHistory()));
                }
                break;
            case FEEDING_HABITS:
                FeedingHabitsRecord feedingHabitsRecord = ig.getFeedingHabitsRecord();
                stringBuilder.append("<h4>" + getString(R.string.water_cup) + "</h4>").append(FeendingHabitsRecordType.OneDayFeedingAmount
                        .getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("<h4>" + getString(R.string.breakfast) + "</h4>").append(FeendingHabitsRecordType.OneDayFeedingAmount
                        .getStringPTBR(feedingHabitsRecord.getBreakfastDailyFrequency()));
                for (WeeklyFoodRecord weeklyFoodRecord : feedingHabitsRecord.getWeeklyFeedingHabits())
                    stringBuilder
                            .append("<h4>" + FoodType.getStringPTBR(weeklyFoodRecord.getFood()))
                            .append("</h4><p>")
                            .append(FeendingHabitsRecordType.SevenDaysFeedingFrequency
                                    .getStringPTBR(weeklyFoodRecord.getSevenDaysFreq()));
                break;
            case PHYSICAL_ACTIVITY:
                PhysicalActivityHabit physicalActivityHabit = ig.getPhysicalActivityHabit();
                stringBuilder.append("<h4>" + getString(R.string.sports_in_week) + "</h4>");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append("<p>" + SportsType.getStringPtBr(context, sport) + "</p>");
                }
                stringBuilder.append("<h4>" + getString(R.string.physical_school) + "</h4>")
                        .append(SchoolActivityFrequencyType
                                .getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
            case FAMILY_COHESION:
                FamilyCohesionRecord familyCohesionRecord = ig.getFamilyCohesionRecord();
                stringBuilder.append("<h4>" + getString(R.string.help_family) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("</p><h4>" + getString(R.string.approval_friends) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFriendshipApprovalFreq()))
                        .append("</p><h4>" + getString(R.string.only_family) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyOnlyPreferenceFreq()))
                        .append("</p><h4>" + getString(R.string.not_strangers) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyMutualAidFreq()))
                        .append("</p><h4>" + getString(R.string.family_freetime) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFreeTimeTogetherFreq()))
                        .append("</p><h4>" + getString(R.string.family_union) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyProximityPerceptionFreq()))
                        .append("</p><h4>" + getString(R.string.family_share) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getAllFamilyTasksFreq()))
                        .append("</p><h4>" + getString(R.string.easy_family) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyTasksOpportunityFreq()))
                        .append("</p><h4>" + getString(R.string.family_decision) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("</p><h4>" + getString(R.string.familiy_union_important) + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyUnionRelevanceFreq()));
                break;
            case ORAL_HEALTH:
                OralHealthRecord oralHealthRecord = ig.getOralHealthRecord();
                stringBuilder.append("</p><h4>" + getString(R.string.tooth_higien) + "</h4><p>")
                        .append(ToothLesionType.TeethBrushingFreq.getStringPtBr(oralHealthRecord.getTeethBrushingFreq()));
                if (ig.getOralHealthRecord() == null) break;

                List<ToothLesion> toothLesions = oralHealthRecord.getToothLesions();
                ToothLesion whiteSpotLesionDeciduousTooth = new ToothLesion("deciduous_tooth", "white_spot_lesion");
                ToothLesion whiteSpotLesionPermanentTooth = new ToothLesion("permanent_tooth", "white_spot_lesion");
                ToothLesion cavitatedLesionDeciduousTooth = new ToothLesion("deciduous_tooth", "cavitated_lesion");
                ToothLesion cavitatedLesionPermanentTooth = new ToothLesion("permanent_tooth", "cavitated_lesion");

                stringBuilder.append("</p><h4>" + getString(R.string.white_spot_lesion_deciduous_tooth) + "</h4><p>");
                if (toothLesions.contains(whiteSpotLesionDeciduousTooth))
                    stringBuilder.append(getString(R.string.yes_text) + "</p>");
                else stringBuilder.append(getString(R.string.no_text) + "</p>");

                stringBuilder.append("</p><h4>" + getString(R.string.white_spot_lesion_permanent_tooth) + "</h4><p>");
                if (toothLesions.contains(whiteSpotLesionPermanentTooth))
                    stringBuilder.append(getString(R.string.yes_text) + "</p>");
                else stringBuilder.append(getString(R.string.no_text) + "</p>");

                stringBuilder.append("</p><h4>" + getString(R.string.cavitated_lesion_deciduous_tooth) + "</h4><p>");
                if (toothLesions.contains(cavitatedLesionDeciduousTooth))
                    stringBuilder.append(getString(R.string.yes_text) + "</p>");
                else stringBuilder.append(getString(R.string.no_text) + "</p>");

                stringBuilder.append("</p><h4>" + getString(R.string.cavitated_lesion_permanent_tooth) + "</h4><p>");
                if (toothLesions.contains(cavitatedLesionPermanentTooth))
                    stringBuilder.append(getString(R.string.yes_text) + "</p>");
                else stringBuilder.append(getString(R.string.no_text) + "</p>");
//
//                if (oralHealthRecord.getToothLesions().isEmpty()) {
//                    stringBuilder.append("<p>" + "Não possui algum tipo de cárie dentária" + "</p>");
//                } else
//                    for (ToothLesion toothLesion : oralHealthRecord.getToothLesions()) {
//                        stringBuilder.append("<h4>" + ToothLesionType.LesionType.getStringPtBr(toothLesion.getLesionType()))
//                                .append(" em ")
//                                .append(ToothLesionType.ToothType.getStringPtbr(toothLesion.getToothType()) + "</h4>");
//                    }
                break;
            case SOCIODEMOGRAPHICS:
                SociodemographicRecord sociodemographicRecord = ig.getSociodemographicRecord();
                stringBuilder.append("<h4>" + getString(R.string.color_race) + "</h4><p>")
                        .append(SociodemographicType.ColorRace.getStringPtBr(sociodemographicRecord.getColorRace()))
                        .append("</p><h4>" + getString(R.string.schoolarity_mother) + "</h4><p>")
                        .append(SociodemographicType.MotherScholarity.getStringPtBr(sociodemographicRecord.getMotherScholarity()))
                        .append("</p><h4>" + getString(R.string.people_in_home) + "</h4><p>")
                        .append(sociodemographicRecord.getPeopleInHome());
                break;
        }
        h.messageText.setText(Html.fromHtml(stringBuilder.toString()));
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
    }
}