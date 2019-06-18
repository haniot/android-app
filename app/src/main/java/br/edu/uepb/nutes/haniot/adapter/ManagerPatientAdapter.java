package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.QuizOdontologyActivity;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PatientsType;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerPatientAdapter extends BaseAdapter<Patient> {

    private final String LOG = "ManagerPatientAdapter";
    private final Context context;
    private AppPreferencesHelper appPreferencesHelper;
    private ActionsPatientListener actionsPatientListener;

    public void setPatientActionListener(ActionsPatientListener mListener) {
        actionsPatientListener = mListener;
    }

    /**
     * Constructor.
     *
     * @param context {@link Context}
     */
    public ManagerPatientAdapter(Context context) {
        this.context = context;
        this.appPreferencesHelper = AppPreferencesHelper.getInstance(context);
    }

    private String calculateAge(String birthDate) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(birthDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return "";

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        return age + " anos";
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_patient, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ManagerPatientViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Patient> itemsList) {
        if (holder instanceof ManagerPatientAdapter.ManagerPatientViewHolder) {
            final Patient patient = itemsList.get(position);
            ManagerPatientViewHolder h = (ManagerPatientViewHolder) holder;

            h.textName.setText(patient.getName());
            h.textName.setEllipsize(TextUtils.TruncateAt.END);
            h.textAge.setText(calculateAge(patient.getBirthDate()));
            if (patient.getGender().equals(PatientsType.GenderType.FEMALE))
                h.profile.setImageResource(R.drawable.x_girl);
            else h.profile.setImageResource(R.drawable.x_boy);

            h.mView.setOnClickListener(v -> {
                actionsPatientListener.onItemClick(patient);
            });

            h.btnMore.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, ((ManagerPatientViewHolder) holder).btnMore);
                popup.inflate(R.menu.menu_patient_actions);
                if (appPreferencesHelper.getUserLogged().getHealthArea().equals("nutrition"))
                    popup.getMenu().getItem(2).setVisible(false);
                else if (appPreferencesHelper.getUserLogged().getHealthArea().equals("dentistry")) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                }
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.remove:
                            actionsPatientListener.onMenuContextClick(h.btnMore, patient);
                            break;
                        case R.id.nutrition_quiz:
                            actionsPatientListener.onMenuClick("quiz_nutrition", patient);
                            break;
                        case R.id.odontotoly_quiz:
                            actionsPatientListener.onMenuClick("quiz_dentistry", patient);
                            break;
                        case R.id.nutrition_evaluation:
                            actionsPatientListener.onMenuClick("nutrition_evaluation", patient);
                            break;
                        case R.id.historic_quiz:
                            actionsPatientListener.onMenuClick("historic_quiz", patient);
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                //displaying the popup
                popup.show();
            });

            h.btnEdit.setOnClickListener(v -> {
                actionsPatientListener.onMenuContextClick(h.btnEdit, patient);
            });
            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ManagerPatientViewHolder) holder).clearAnimation();
    }

    public static class ManagerPatientViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        @BindView(R.id.btnEditChildren)
        ImageView btnEdit;
        @BindView(R.id.btnMore)
        ImageView btnMore;
        @BindView(R.id.textNameChildValue)
        TextView textName;
        @BindView(R.id.textAge)
        TextView textAge;
        @BindView(R.id.textLetter)
        ImageView profile;

        public ManagerPatientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }

    public interface ActionsPatientListener extends OnRecyclerViewListener<Patient> {

        void onMenuClick(String action, Patient patient);
    }
}
