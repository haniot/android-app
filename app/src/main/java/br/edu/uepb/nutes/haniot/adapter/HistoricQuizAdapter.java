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

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
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
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
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
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
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

public class HistoricQuizAdapter extends CustomExpandableRecyclerViewAdapter<HistoricQuizAdapter.HeaderViewHolder,
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

    public void addAll(List<? extends ExpandableGroup> groups) {
        Collection collection = groups;
        getGroups().addAll(collection);
        notifyGroupDataChanged();
        notifyDataSetChanged();
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

        h.checkItem.setOnClickListener(v -> {
            mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation(), ((GroupItemEvaluation) group).getIdGroup());
        });

        StringBuilder stringBuilder = new StringBuilder();

        Log.w("AAAAAA", "Type Evaluation: " + ig.getTypeEvaluation());

        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS:
                SleepHabit sleepHabit = ig.getSleepHabit();
                stringBuilder.append("<h4>" + "Dorme às " + "</h4><p>").append(sleepHabit.getWeekDaySleep())
                        .append(" horas");
                stringBuilder.append("<h4>" + "Acorda às " + "</h4><p>").append(sleepHabit.getWeekDayWakeUp())
                        .append(" horas");
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
                stringBuilder.append("<h4>" + "Copos de água por dia" + "</h4>").append(FeendingHabitsRecordType.OneDayFeedingAmount
                        .getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("<h4>" + "Café da manhã" + "</h4>").append(FeendingHabitsRecordType.OneDayFeedingAmount
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
                stringBuilder.append("<h4>" + "Esportes praticados durante a semana" + "</h4>");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append("<p>" + SportsType.getStringPtBr(context, sport) + "</p>");
                }
                stringBuilder.append("<h4>" + "Frequência de atividades físicas na escola" + "</h4>")
                        .append(SchoolActivityFrequencyType
                                .getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
            case FAMILY_COHESION:
                FamilyCohesionRecord familyCohesionRecord = ig.getFamilyCohesionRecord();
                stringBuilder.append("<h4>" + "Os membros da família pedem ajuda uns aos outros?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("</p><h4>" + "Aprovamos os amigos que cada um tem ?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFriendshipApprovalFreq()))
                        .append("</p><h4>" + "Gostamos de fazer coisas apenas com nossa família?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyOnlyPreferenceFreq()))
                        .append("</p><h4>" + "Os membros da família sentem-se mais próximos entre si que com pessoas estranhas à família?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyMutualAidFreq()))
                        .append("</p><h4>" + "Os membros da família gostam de passar o tempo livre juntos?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFreeTimeTogetherFreq()))
                        .append("</p><h4>" + "Os membros da família se sentem muito próximos uns aos outros?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyProximityPerceptionFreq()))
                        .append("</p><h4>" + "Estamos todos presentes quando compartilhamos atividades em nossa família?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getAllFamilyTasksFreq()))
                        .append("</p><h4>" + "Facilmente nos ocorrem que podemos fazer juntos, em família?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyTasksOpportunityFreq()))
                        .append("</p><h4>" + "Os membros da família consultam outras pessoas da família para tomarem suas decisões?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("</p><h4>" + "União familiar é muito importante?" + "</h4><p>")
                        .append(FrequencyAnswersType.Frequency.getStringPTBR(familyCohesionRecord.getFamilyUnionRelevanceFreq()));
                break;
            case ORAL_HEALTH:
                OralHealthRecord oralHealthRecord = ig.getOralHealthRecord();
                stringBuilder.append("</p><h4>" + "Escovação de dentes ao dia" + "</h4><p>")
                        .append(ToothLesionType.TeethBrushingFreq.getStringPtBr(oralHealthRecord.getTeethBrushingFreq()));
                if (oralHealthRecord.getToothLesions().isEmpty()) {
                    stringBuilder.append("<p>" + "Não possui algum tipo de cárie dentária" + "</p>");
                } else
                    for (ToothLesion toothLesion : oralHealthRecord.getToothLesions()) {
                        stringBuilder.append("<h4>" + ToothLesionType.LesionType.getStringPtBr(toothLesion.getLesionType()))
                                .append(" em ")
                                .append(ToothLesionType.ToothType.getStringPtbr(toothLesion.getToothType()) + "</h4>");
                    }
                break;
            case SOCIODEMOGRAPHICS:
                SociodemographicRecord sociodemographicRecord = ig.getSociodemographicRecord();
                stringBuilder.append("<h4>" + "Cor/raça" + "</h4><p>")
                        .append(SociodemographicType.ColorRace.getStringPtBr(sociodemographicRecord.getColorRace()))
                        .append("</p><h4>" + "Escolaridade da mãe" + "</h4><p>")
                        .append(SociodemographicType.MotherScholarity.getStringPtBr(sociodemographicRecord.getMotherScholarity()))
                        .append("</p><h4>" + "Número de pessoas em casa" + "</h4><p>")
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