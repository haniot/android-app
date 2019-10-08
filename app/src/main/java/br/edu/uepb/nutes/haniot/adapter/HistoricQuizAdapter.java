package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
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

import java.util.Collection;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FoodType;
import br.edu.uepb.nutes.haniot.data.model.GroupItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.ToothLesion;
import br.edu.uepb.nutes.haniot.data.model.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.data.model.type.ChronicDiseaseType;
import br.edu.uepb.nutes.haniot.data.model.type.FeendingHabitsRecordType;
import br.edu.uepb.nutes.haniot.data.model.type.FrequencyAnswersType;
import br.edu.uepb.nutes.haniot.data.model.type.SchoolActivityFrequencyType;
import br.edu.uepb.nutes.haniot.data.model.type.SociodemographicType;
import br.edu.uepb.nutes.haniot.data.model.type.SportsType;
import br.edu.uepb.nutes.haniot.data.model.type.ToothLesionType;
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
    public OnClick mListener;

    public HistoricQuizAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
    }

    public void addAll(List<? extends ExpandableGroup> groups) {
        getGroups().addAll((Collection) groups);
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
     * @param view     {@link View}
     * @param position int
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
     * @param h  {@link ViewHolder}
     * @param ig {@link ItemEvaluation}
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
     * @param h  {@link ViewHolder}
     * @param ig {@link ItemEvaluation}
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

    private String getString(int id) {
        String string = context.getResources().getString(id);
        return string;
    }

    /**
     * View responsible for displaying questionnaire data.
     *
     * @param h     {@link ViewHolder}
     * @param ig    {@link ItemEvaluation}
     * @param group {@link ExpandableGroup}
     */
    private void createQuizView(ViewHolder h, ItemEvaluation ig, ExpandableGroup group) {
        h.messageText.setVisibility(View.VISIBLE);

        h.checkItem.setOnClickListener(v -> {
            mListener.onAddItemClick(ig.getTitle(), ig.getTypeEvaluation(), ((GroupItemEvaluation) group).getIdGroup());
        });
        StringBuilder stringBuilder = new StringBuilder();

        switch (ig.getTypeEvaluation()) {
            case SLEEP_HABITS: {
                SleepHabit sleepHabit = ig.getSleepHabit();
                stringBuilder.append("<h4>")
                        .append(getString(R.string.sleep_at)).append(" </h4><p>")
                        .append(sleepHabit.getWeekDaySleep())
                        .append(getString(R.string.hours));
                stringBuilder.append("<h4>")
                        .append(getString(R.string.wakeup_at))
                        .append(" </h4><p>")
                        .append(sleepHabit.getWeekDayWakeUp())
                        .append(getString(R.string.hours));
                break;
            }
            case MEDICAL_RECORDS: {
                MedicalRecord medicalRecord = ig.getMedicalRecord();
                if (medicalRecord == null) return;
                if (medicalRecord.getChronicDiseases() != null) {
                    for (ChronicDisease chronicDisease : medicalRecord.getChronicDiseases())
                        stringBuilder.append("<h4>").append(ChronicDiseaseType.ChronicDisease
                                .getString_PTBR(chronicDisease.getType()))
                                .append("</h4><p>")
                                .append(ChronicDiseaseType.DisieaseHistory
                                        .getStringPTBR(chronicDisease.getDiseaseHistory()));
                }
                break;
            }
            case FEEDING_HABITS: {
                FeedingHabitsRecord feedingHabitsRecord = ig.getFeedingHabitsRecord();
                stringBuilder.append("<h4>").append(getString(R.string.water_cup)).append("</h4>")
                        .append(FeendingHabitsRecordType.OneDayFeedingAmount
                                .getStringPTBR(feedingHabitsRecord.getDailyWaterGlasses()));
                stringBuilder.append("<h4>").append(getString(R.string.breakfast)).append("</h4>")
                        .append(FeendingHabitsRecordType.OneDayFeedingAmount
                                .getStringPTBR(feedingHabitsRecord.getBreakfastDailyFrequency()));
                for (WeeklyFoodRecord weeklyFoodRecord : feedingHabitsRecord.getWeeklyFeedingHabits())
                    stringBuilder.append("<h4>").append(FoodType.getStringPTBR(weeklyFoodRecord.getFood()))
                            .append("</h4><p>")
                            .append(FeendingHabitsRecordType.SevenDaysFeedingFrequency
                                    .getStringPTBR(weeklyFoodRecord.getSevenDaysFreq()));
                break;
            }
            case PHYSICAL_ACTIVITY: {
                PhysicalActivityHabit physicalActivityHabit = ig.getPhysicalActivityHabit();
                stringBuilder.append("<h4>").append(getString(R.string.sports_in_week)).append("</h4>");
                for (String sport : physicalActivityHabit.getWeeklyActivities()) {
                    stringBuilder.append("<p>").append(SportsType.getStringPtBr(context, sport)).append("</p>");
                }
                stringBuilder.append("<h4>").append(getString(R.string.physical_school)).append("</h4>")
                        .append(SchoolActivityFrequencyType
                                .getStringPTBR(physicalActivityHabit.getSchoolActivityFreq()));
                break;
            }
            case FAMILY_COHESION: {
                FamilyCohesionRecord familyCohesionRecord = ig.getFamilyCohesionRecord();
                stringBuilder.append("<h4>").append(getString(R.string.help_family)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq()))
                        .append("</p><h4>").append(getString(R.string.approval_friends)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFriendshipApprovalFreq()))
                        .append("</p><h4>").append(getString(R.string.only_family)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyOnlyPreferenceFreq()))
                        .append("</p><h4>").append(getString(R.string.not_strangers)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyMutualAidFreq()))
                        .append("</p><h4>").append(getString(R.string.family_freetime)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFreeTimeTogetherFreq()))
                        .append("</p><h4>").append(getString(R.string.family_union)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyProximityPerceptionFreq()))
                        .append("</p><h4>").append(getString(R.string.family_share)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getAllFamilyTasksFreq()))
                        .append("</p><h4>").append(getString(R.string.easy_family)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyTasksOpportunityFreq()))
                        .append("</p><h4>").append(getString(R.string.family_decision)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyDecisionSupportFreq())).append("</p><h4>")
                        .append(getString(R.string.familiy_union_important)).append("</h4><p>")
                        .append(FrequencyAnswersType.Frequency
                                .getStringPTBR(familyCohesionRecord.getFamilyUnionRelevanceFreq()));
                break;
            }
            case ORAL_HEALTH: {
                OralHealthRecord oralHealthRecord = ig.getOralHealthRecord();
                if (ig.getOralHealthRecord() == null) break;
                stringBuilder.append("</p><h4>").append(getString(R.string.tooth_higien)).append("</h4><p>")
                        .append(ToothLesionType.TeethBrushingFreq
                                .getStringPtBr(oralHealthRecord.getTeethBrushingFreq()));

                List<ToothLesion> toothLesions = oralHealthRecord.getToothLesions();
                ToothLesion whiteSpotLesionDeciduousTooth = new ToothLesion("deciduous_tooth", "white_spot_lesion");
                ToothLesion whiteSpotLesionPermanentTooth = new ToothLesion("permanent_tooth", "white_spot_lesion");
                ToothLesion cavitatedLesionDeciduousTooth = new ToothLesion("deciduous_tooth", "cavitated_lesion");
                ToothLesion cavitatedLesionPermanentTooth = new ToothLesion("permanent_tooth", "cavitated_lesion");

                stringBuilder.append("</p><h4>").append(getString(R.string.white_spot_lesion_deciduous_tooth)).append("</h4><p>");
                if (toothLesions != null && toothLesions.contains(whiteSpotLesionDeciduousTooth))
                    stringBuilder.append(getString(R.string.yes_text)).append("</p>");
                else stringBuilder.append(getString(R.string.no_text)).append("</p>");

                stringBuilder.append("</p><h4>").append(getString(R.string.white_spot_lesion_permanent_tooth)).append("</h4><p>");
                if (toothLesions != null && toothLesions.contains(whiteSpotLesionPermanentTooth))
                    stringBuilder.append(getString(R.string.yes_text)).append("</p>");
                else stringBuilder.append(getString(R.string.no_text)).append("</p>");

                stringBuilder.append("</p><h4>").append(getString(R.string.cavitated_lesion_deciduous_tooth)).append("</h4><p>");
                if (toothLesions != null && toothLesions.contains(cavitatedLesionDeciduousTooth))
                    stringBuilder.append(getString(R.string.yes_text)).append("</p>");
                else stringBuilder.append(getString(R.string.no_text)).append("</p>");

                stringBuilder.append("</p><h4>").append(getString(R.string.cavitated_lesion_permanent_tooth)).append("</h4><p>");
                if (toothLesions != null && toothLesions.contains(cavitatedLesionPermanentTooth))
                    stringBuilder.append(getString(R.string.yes_text)).append("</p>");
                else stringBuilder.append(getString(R.string.no_text)).append("</p>");
                break;
            }
            case SOCIODEMOGRAPHICS: {
                SociodemographicRecord sociodemographicRecord = ig.getSociodemographicRecord();
                stringBuilder.append("<h4>").append(getString(R.string.color_race)).append("</h4><p>")
                        .append(SociodemographicType.ColorRace
                                .getStringPtBr(sociodemographicRecord.getColorRace()))
                        .append("</p><h4>").append(getString(R.string.schoolarity_mother)).append("</h4><p>")
                        .append(SociodemographicType.MotherScholarity
                                .getStringPtBr(sociodemographicRecord.getMotherScholarity()))
                        .append("</p><h4>").append(getString(R.string.people_in_home)).append("</h4><p>")
                        .append(sociodemographicRecord.getPeopleInHome());
                break;
            }
            default:
                break;
        }
        h.messageText.setText(Html.fromHtml(stringBuilder.toString()));
    }

    /**
     * View displayed while downloading the data.
     *
     * @param h  {@link ViewHolder}
     * @param ig {@link ItemEvaluation}
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

        HeaderViewHolder(View itemView) {
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