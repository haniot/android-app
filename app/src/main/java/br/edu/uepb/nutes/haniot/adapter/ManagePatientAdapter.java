package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.PatientRegisterActivity;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePatientAdapter extends RecyclerView.Adapter<ManagePatientAdapter.ManagePatientViewHolder> {

    private List<Patient> itemList;
    private List<Patient> itemListCopy = new ArrayList<>();
    private Context context;
    private String searchQuerry = "";
    private String t = "TESTE";
    public final int REMOVE_TYPE_NOT_FILTERED = 1;
    public final int REMOVE_TYPE_FILTERED = 2;
    private final int EMPTY_VIEW = 77777;
    private OnRecyclerViewListener onRecyclerViewListener;

    public ManagePatientAdapter(List<Patient> patientList, Context context, OnRecyclerViewListener onRecyclerViewListener) {

        this.itemList = patientList;
        this.itemListCopy.addAll(patientList);
        this.context = context;
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @NonNull
    @Override
    public ManagePatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Os itens não estavam preenchendo a tela na api 19, inflando dessa forma o bug é resolvido.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;

//        test if the list is empty and return the empty view if yes;
        if (viewType == EMPTY_VIEW) {
            view = inflater.from(parent.getContext()).inflate(R.layout.patient_empty_view, parent, false);
            ManagePatientViewHolder holder = new ManagePatientViewHolder(view, "");
            return holder;
        } else {
            view = inflater.from(parent.getContext()).inflate(R.layout.item_children, null, false);
            ManagePatientViewHolder holder = new ManagePatientViewHolder(view, "notEmpty");
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ManagePatientViewHolder holder, int position) {

        if (!(getItemViewType(position) == EMPTY_VIEW)) {
            Patient patient = itemList.get(position);
            if (patient.getGender().equals("male"))
                holder.profile.setImageResource(R.drawable.x_boy);
            else
                holder.profile.setImageResource(R.drawable.x_girl);
            holder.textName.setText(String.format("%s %s", patient.getFirstName(), patient.getLastName()));

            holder.textAge.setText(getAge(patient.getBirthDate()));
            holder.btnEdit.setOnClickListener(c -> {
//                //TODO Usar interface na activity
//                final Intent intent = new Intent(context, PatientRegisterActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(holder.itemView,
//                        0,
//                        0,
//                        holder.itemView.getWidth(),
//                        holder.itemView.getHeight()).toBundle();
//                intent.putExtra("Patient", patient.getId());
//                context.startActivity(intent, bundle);
                onRecyclerViewListener.onMenuContextClick(holder.btnEdit, patient);
            });
            holder.btnDelete.setOnClickListener(d -> {
                onRecyclerViewListener.onMenuContextClick(holder.btnDelete, patient);
//                //TODO Usar interface na activity
//                int oldId = searchPositionByChildrenId(patient.get_id());
//                if (oldId != -1) {
//                    if (this.searchQuerry.isEmpty()) {
//                        removeItem(patient, oldId, REMOVE_TYPE_NOT_FILTERED);
//                    } else {
//                        removeItem(patient, oldId, REMOVE_TYPE_FILTERED);
//                    }
//                    Snackbar snackbar = Snackbar
//                            .make(holder.itemView, context.getResources().getString(R.string.patient_removed)
//                                    , Snackbar.LENGTH_LONG).setAction(context.getResources()
//                                    .getString(R.string.undo), view -> {
//                                restoreItem(patient, position, holder.itemView);
//                            });
//                    snackbar.show();
//                    if (itemListCopy.size() == 0) {
//                        Toast.makeText(context, context.getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
//                    }
//                }
            });
            holder.itemView.setOnClickListener(c -> {
                //TODO Usar interface na activity
                onRecyclerViewListener.onItemClick(patient);
            });

        }
    }

    private String getAge(String birthday) {
        Date date = null;
        date = DateUtils.fromISO8601(birthday);
        if (date == null) return "";
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);
        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);
        dob.set(year, month + 1, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age + " anos";
    }

    public int searchPositionByChildrenId(String _id) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).get_id().equals(_id)) {
                return i;
            }
        }

        return -1;
    }

    public int getItemsSize() {
        return this.itemList.size();
    }

    public int searchOldPatientPosition(Patient patient) {
        return itemListCopy.lastIndexOf(patient);
    }

    public void setSearchQuerry(String searchQuerry) {
        this.searchQuerry = searchQuerry;
    }

    public String getSearchQuerry() {
        return searchQuerry;
    }

    public void restoreItem(Patient patient, int index, View view) {
        if (itemList.isEmpty()) {
            notifyDataSetChanged();
        }
        itemListCopy.add(index, patient);
        itemList.add(index, patient);
        notifyItemInserted(index);
    }

    public void removeItem(Patient patient, int oldPosition, final int updateType) {

        if (this.itemListCopy.size() > 0) {
            this.itemListCopy.remove(patient);
            this.itemList.remove(patient);
            if (updateType == REMOVE_TYPE_FILTERED) {
//                    notifyDataSetChanged();
                notifyItemRemoved(oldPosition);
                notifyItemRangeChanged(oldPosition, itemList.size());
                Log.d(t, "tipo filtrado \n ");
            } else {
                Log.d(t, "Posição: " + oldPosition);
                notifyItemRemoved(oldPosition);
                notifyItemRangeChanged(oldPosition, itemListCopy.size());
            }

        }
    }

    public List<Patient> getData() {
        return this.itemList;
    }

    public Patient getPatient(int index) {

        if (itemList.size() > 0 && itemList.size() >= index) {
            return itemList.get(index);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return itemList.size() > 0 ? itemList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public void filter(String text) {
        if (text.isEmpty()) {
            itemList.clear();
            itemList.addAll(itemListCopy);
        } else {
            ArrayList<Patient> result = new ArrayList<>();
            for (Patient patient : itemListCopy) {
                if (patient.get_id().toLowerCase().contains(text.toLowerCase())
                        //|| patient.getRegisterDate().toLowerCase().equalsIgnoreCase(text.toLowerCase())
                        || patient.getFirstName().toLowerCase().contains(text.toLowerCase())) {

                    result.add(patient);
                    Log.d("TESTE", patient.getFirstName());
                }
            }
            itemList.clear();
            itemList.addAll(result);
        }
        notifyDataSetChanged();
    }

    public static class ManagePatientViewHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.textIdNumberChildren)
//        TextView textId;
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

        public ManagePatientViewHolder(View itemView, String type) {
            super(itemView);
//            test if the view is empty, if yes, the items of patient are not binded;
            if (!type.isEmpty())
                ButterKnife.bind(this, itemView);
        }

    }

}
