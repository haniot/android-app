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
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
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

public class HistoricQuizAdapter extends ExpandableRecyclerViewAdapter<HistoricQuizAdapter.HeaderViewHolder,
        HistoricQuizAdapter.ViewHolder> {
    private Context context;
    private int lastPosition = -1;
    private DecimalFormat decimalFormat;
    public OnClick mListener;

    public HistoricQuizAdapter(List<? extends ExpandableGroup> groups, Context context) {
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
        View itemView = mInflater.inflate(R.layout.item_quiz, parent, false);
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
        } else if (ig.getTypeHeader() == ItemEvaluation.TYPE_EMPTY) {
            createEmptyView(h, ig);
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
        h.checkItem.setVisibility(View.VISIBLE);
        h.messageText.setVisibility(View.VISIBLE);
        h.loading.setVisibility(View.GONE);
        h.imageItem.setImageResource(ig.getIcon());
        h.textDescription.setText(ig.getTitle());
    }

    /**
     * View displayed when there is no data.
     *
     * @param h
     * @param ig
     */
    private void createErrorView(ViewHolder h, ItemEvaluation ig) {
        h.messageText.setVisibility(View.VISIBLE);
        h.messageText.setText(context.getResources().getString(R.string.evaluation_error_message));
        h.checkItem.setVisibility(View.INVISIBLE);
    }

    private void createEmptyView(ViewHolder h, ItemEvaluation ig) {
        h.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation());
            }
        });
        h.messageText.setVisibility(View.VISIBLE);
        h.messageText.setText(context.getResources().getString(R.string.evaluation_empty_message));
        h.checkItem.setVisibility(View.INVISIBLE);
    }

    /**
     * View responsible for displaying questionnaire data.
     *
     * @param h
     * @param ig
     * @param group
     */
    private void createQuizView(ViewHolder h, ItemEvaluation ig, ExpandableGroup group) {
        h.messageText.setVisibility(View.VISIBLE);

        h.mView.setOnClickListener(v -> {
            mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation(), ((GroupItemEvaluation) group).getIdGroup());
        });

        StringBuilder stringBuilder = new StringBuilder();

        Log.w("AAAAAA", "Type Evaluation: " + ig.getTypeEvaluation());

        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS:
                SleepHabit sleepHabit = ig.getSleepHabit();
                stringBuilder.append("\nDorme às ").append(sleepHabit.getWeekDaySleep())
                        .append(" horas");
                stringBuilder.append("\nAcorda às ").append(sleepHabit.getWeekDayWakeUp())
                        .append(" horas");
                break;
            case MEDICAL_RECORDS:
                MedicalRecord medicalRecord = ig.getMedicalRecord();
                if (medicalRecord == null) return;
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
                stringBuilder.append("\nCopos de água por dia: ").append(FeendingHabitsRecordType.OneDayFeedingAmount
                        .getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("\nCafé da manhã: ").append(FeendingHabitsRecordType.OneDayFeedingAmount
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
                stringBuilder.append("\nEsportes praticados durante a semana: \n");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append(sport).append("\n");
                }
                stringBuilder.append("\nFrequência de atividades físicas na escola: \n")
                        .append(SchoolActivityFrequencyType
                                .getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
            case FAMILY_COHESION:
                FamilyCohesionRecord familyCohesionRecord = ig.getFamilyCohesionRecord();
                stringBuilder.append("Os membros da família pedem ajuda uns aos outros?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Aprovamos os amigos que cada um tem?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFriendshipApprovalFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Gostamos de fazer coisas apenas com nossa família?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyOnlyPreferenceFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Os membros da família sentem-se mais próximos entre si que com pessoas estranhas à família?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyMutualAidFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Os membros da família gostam de passar o tempo livre juntos?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFreeTimeTogetherFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Os membros da família se sentem muito próximos uns aos outros?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyProximityPerceptionFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Estamos todos presentes quando compartilhamos atividades em nossa família?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getAllFamilyTasksFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Facilmente nos ocorrem que podemos fazer juntos, em família?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyTasksOpportunityFreq()))
                        .append("\n")
                        .append("\n")
                        .append("Os membros da família consultam outras pessoas da família para tomarem suas decisões?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("\n")
                        .append("\n")
                        .append("União familiar é muito importante?")
                        .append("\n")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyUnionRelevanceFreq()));
                break;
            case ORAL_HEALTH:
                OralHealthRecord oralHealthRecord = ig.getOralHealthRecord();
                stringBuilder.append("Escovação de dentes ao dia: ")
                        .append(ToothLesionType.TeethBrushingFreq.getStringPtBr(oralHealthRecord.getTeethBrushingFreq()))
                        .append("\n")
                        .append("\n");
                if (oralHealthRecord.getToothLesions().isEmpty()) {
                    stringBuilder.append("Não possui algum tipo de cárie dentária");
                } else
                    for (ToothLesion toothLesion : oralHealthRecord.getToothLesions()) {
                        stringBuilder.append(ToothLesionType.LesionType.getStringPtBr(toothLesion.getLesionType()))
                                .append(" em ")
                                .append(ToothLesionType.ToothType.getStringPtbr(toothLesion.getToothType()))
                                .append("\n");
                    }
                break;
            case SOCIODEMOGRAPHICS:
                SociodemographicRecord sociodemographicRecord = ig.getSociodemographicRecord();
                stringBuilder.append("Cor/raça: ")
                        .append("\n")
                        .append(SociodemographicType.ColorRace.getStringPtBr(sociodemographicRecord.getColorRace()))
                        .append("\n")
                        .append("\n")
                        .append("Escolaridade da mãe: ")
                        .append("\n")
                        .append(SociodemographicType.MotherScholarity.getStringPtBr(sociodemographicRecord.getMotherScholarity()))
                        .append("\n")
                        .append("\n")
                        .append("Número de pessoas em casa: ")
                        .append("\n")
                        .append(sociodemographicRecord.getPeopleInHome());
                break;
        }
        h.messageText.setText(stringBuilder);
    }

    /**
     * View displayed while downloading the data.
     *
     * @param h
     * @param ig
     */
    private void createLoadingView(ViewHolder h, ItemEvaluation ig) {
        h.loading.setVisibility(View.VISIBLE);
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
        @BindView(R.id.quiz_text)
        TextView messageText;
        @BindView(R.id.check_item)
        ImageView checkItem;
        @BindView(R.id.loading)
        ProgressBar loading;
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

        void onAddItemClick(String name, int type, String idQuiz);

        void onSelectClick(ItemEvaluation itemEvaluation, String idQuiz);

    }
}