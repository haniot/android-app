package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.PatientRegisterActivity;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PatientsType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerPatientAdapter extends BaseAdapter<Patient> {

    private final String LOG = "ManagerPatientAdapter";
    private final Context context;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public ManagerPatientAdapter(Context context) {
        this.context = context;
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

//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--;
//        }
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

            h.textName.setText(String.format("%s %s", patient.getFirstName(), patient.getLastName()));
            h.textAge.setText(calculateAge(patient.getBirthDate()));
            if (patient.getGender().equals(PatientsType.GenderType.FEMALE))
                h.profile.setImageResource(R.drawable.x_girl);
            else h.profile.setImageResource(R.drawable.x_boy);

            h.mView.setOnClickListener(v -> {
                if (ManagerPatientAdapter.super.mListener != null) {
                    ManagerPatientAdapter.super.mListener.onItemClick(patient);
                }
            });

            h.btnDelete.setOnClickListener(v -> {
                if (ManagerPatientAdapter.super.mListener != null) {
                    ManagerPatientAdapter.super.mListener.onMenuContextClick(h.btnDelete, patient);
                }
            });

            h.btnEdit.setOnClickListener(v -> {
                if (ManagerPatientAdapter.super.mListener != null) {
                    ManagerPatientAdapter.super.mListener.onMenuContextClick(h.btnEdit, patient);
                }
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
        @BindView(R.id.btnDeleteChild)
        ImageView btnDelete;
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
}
