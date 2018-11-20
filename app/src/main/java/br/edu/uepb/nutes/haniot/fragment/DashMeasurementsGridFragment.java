package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.model.DateChangedEvent;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashMeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<ItemGrid>{

    private ItemGrid igActivity;
    private ItemGrid igGlucose;
    private ItemGrid igPressure;
    private ItemGrid igTemperature;
    private ItemGrid igWeight;
    private ItemGrid igSleep;
    private ItemGrid igHearRate;

    private List<ItemGrid> buttonList = new ArrayList<>();
    private Context mContext;
    private GridDashAdapter mAdapter;

    //    This instance is used to get the preferences of preference screen
    private SharedPreferences preferences;

    // Variables strings of grid
    private String activity = "";
    private String glucose = "";
    private String pressure = "";
    private String temperature = "";
    private String weight = "";
    private String sleep = "";
    private String heartRate = "";

    private String measurementDate = "";

    private String deviceTypeTag;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    private DateChangedEvent measurementsValues;

    public DashMeasurementsGridFragment() {
    }

    public static DashMeasurementsGridFragment newInstance() {
        DashMeasurementsGridFragment fragment = new DashMeasurementsGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(
                getActivity(), R.xml.pref_manage_measurements, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Ajeitar para primeira vez que abrir o app;
        this.measurementsValues = new DateChangedEvent();

        this.igActivity = new ItemGrid();
        igActivity.setContext(getContext());
        igActivity.setIcon(R.drawable.ic_activity);
        igActivity.setDescription(getResources().getString(R.string.activity));
        igActivity.setMeasurementValue(this.measurementsValues.getActivity());
        igActivity.setType(ItemGridType.ACTIVITY);

        this.igGlucose = new ItemGrid();
        igGlucose.setContext(getContext());
        igGlucose.setIcon(R.drawable.ic_blood_glucose);
        igGlucose.setDescription(getResources().getString(R.string.blood_glucose));
        igGlucose.setMeasurementValue(this.measurementsValues.getGlucose());
        igGlucose.setType(ItemGridType.BLOOD_GLUCOSE);

        this.igPressure = new ItemGrid();
        igPressure.setContext(getContext());
        igPressure.setIcon(R.drawable.ic_blood_pressure_64);
        igPressure.setDescription(getResources().getString(R.string.blood_pressure));
        igPressure.setMeasurementValue(this.measurementsValues.getPressure());
        igPressure.setType(ItemGridType.BLOOD_PRESSURE);

        this.igTemperature = new ItemGrid();
        igTemperature.setContext(getContext());
        igTemperature.setIcon(R.drawable.ic_temperature);
        igTemperature.setDescription(getResources().getString(R.string.temperature));
        igTemperature.setMeasurementValue(this.measurementsValues.getTemperature());
        igTemperature.setType(ItemGridType.TEMPERATURE);

        this.igWeight = new ItemGrid();
        igWeight.setContext(getContext());
        igWeight.setIcon(R.drawable.ic_weight);
        igWeight.setDescription(getResources().getString(R.string.weight));
        igWeight.setMeasurementValue(this.measurementsValues.getWeight());
        igWeight.setType(ItemGridType.WEIGHT);

        this.igSleep = new ItemGrid();
        igSleep.setContext(getContext());
        igSleep.setIcon(R.drawable.ic_sleeping);
        igSleep.setDescription(getResources().getString(R.string.sleep));
        igSleep.setMeasurementValue(this.measurementsValues.getSleep());
        igSleep.setType(ItemGridType.SLEEP);

        this.igHearRate = new ItemGrid();
        igHearRate.setContext(getContext());
        igHearRate.setIcon(R.drawable.ic_heart_rate_64);
        igHearRate.setDescription(getResources().getString(R.string.heart_rate));
        igHearRate.setMeasurementValue(this.measurementsValues.getHeartRate());
        igHearRate.setType(ItemGridType.HEART_RATE);

        this.activity = getResources().getString(R.string.activity);
        this.glucose = getResources().getString(R.string.blood_glucose);
        this.pressure = getResources().getString(R.string.blood_pressure);
        this.temperature = getResources().getString(R.string.temperature);
        this.weight = getResources().getString(R.string.weight);
        this.sleep = getResources().getString(R.string.sleep);
        this.heartRate = getResources().getString(R.string.heart_rate);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat spn = new SimpleDateFormat(getResources().getString(R.string.date_format));

        this.measurementDate = spn.format(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash2, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        initComponents();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initComponents() {
//        initRecyclerView();
        updateGrid();
    }

    private void initRecyclerView() {
        mAdapter = new GridDashAdapter(mContext);
        mAdapter.setHasStableIds(true);

        // This method set the same size to all items of grid
        gridMeasurement.setHasFixedSize(true);
        deviceTypeTag = gridMeasurement.getTag().toString();
        /**
         * Set a grid layout to recyclerview,
         * the calculateNoOfColumns was used to set the grid autospacing
         */
        gridMeasurement.setLayoutManager(new GridLayoutManager(mContext,
                calculateNoOfColumns(mContext)));
        gridMeasurement.setItemAnimator(new DefaultItemAnimator());
        gridMeasurement.setNestedScrollingEnabled(false);

        mAdapter.setListener(this);
        gridMeasurement.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) { }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {

                Collections.swap(buttonList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(gridMeasurement);

// Extend the Callback class
//        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
//            //and in your imlpementaion of
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                // get the viewHolder's and target's positions in your adapter data, swap them
//                Collections.swap(buttonList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                // and notify the adapter that its dataset has changed
//                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                return true;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                //TODO
//            }
//
//            //defines the enabled move directions in each state (idle, swiping, dragging).
//            @Override
//            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
//                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
//            }
//        };
//        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
//        ith.attachToRecyclerView(gridMeasurement);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public int calculateNoOfColumns(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = 1;

        if (this.deviceTypeTag.equals("tablet")){

//            260 is the size for tablets
            noOfColumns = (int) (dpWidth / 260);

        }else{

//            160 is the size for smartphones
            noOfColumns = (int) (dpWidth / 160);

        }
        return noOfColumns;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGrid();
    }

    private void updateItemsOfGrid(boolean status, ItemGrid item){
        if (status){
            if (!buttonList.contains(item)){
                buttonList.add(item);
            }
            int index = buttonList.lastIndexOf(item);

            switch (item.getType()){
                case ItemGridType.ACTIVITY:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getActivity());
                    break;

                case ItemGridType.BLOOD_GLUCOSE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getGlucose());
                    break;

                case ItemGridType.BLOOD_PRESSURE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getPressure());
                    break;

                case ItemGridType.TEMPERATURE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues
                            .getTemperature());
                    break;

                case ItemGridType.WEIGHT:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getWeight());
                    break;

                case ItemGridType.SLEEP:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getSleep());
                    break;

                case ItemGridType.HEART_RATE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues
                            .getHeartRate());
                    break;
            }
        }else{

            int index = buttonList.lastIndexOf(item);
            if (index >= 0){
                buttonList.remove(index);
            }

        }
    }

    public void updateGrid() {

        //Pega os dados que foram selecionados nas preferencias
        Boolean activity = getPreferenceBoolean(getResources()
                .getString(R.string.key_activity));

        Boolean bloodGlucose = getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_glucose));

        Boolean bloodPressure = getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_pressure));

        Boolean temperature = getPreferenceBoolean(getResources()
                .getString(R.string.key_temperature));

        Boolean weight = getPreferenceBoolean(getResources()
                .getString(R.string.key_weight));

        Boolean sleep = getPreferenceBoolean(getResources()
                .getString(R.string.key_sleep));

        Boolean heartRate = getPreferenceBoolean(getResources()
                .getString(R.string.key_heart_rate));

        if (buttonList != null && buttonList.isEmpty()){
            if (activity) buttonList.add(this.igActivity);
            if (bloodGlucose) buttonList.add(this.igGlucose);
            if (bloodPressure) buttonList.add(this.igPressure);
            if (temperature) buttonList.add(this.igTemperature);
            if (weight) buttonList.add(this.igWeight);
            if (sleep) buttonList.add(this.igSleep);
            if (heartRate) buttonList.add(this.igHearRate);

        }else if (buttonList != null){

            updateItemsOfGrid(activity,igActivity);
            updateItemsOfGrid(bloodGlucose,igGlucose);
            updateItemsOfGrid(bloodPressure,igPressure);
            updateItemsOfGrid(temperature,igTemperature);
            updateItemsOfGrid(weight,igWeight);
            updateItemsOfGrid(sleep,igSleep);
            updateItemsOfGrid(heartRate,igHearRate);
        }

        mAdapter.clearItems();
        mAdapter.addItems(buttonList);
    }

    //    Method used to get the preferences of sharedpreference screen
    public Boolean getPreferenceBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(ItemGrid item) {

        if (this.activity.equals(item.getDescription())) {//place activity when implemented

        } else if (this.glucose.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), GlucoseActivity.class));

        } else if (this.pressure.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), BloodPressureHDPActivity.class));

        } else if (this.temperature.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), ThermometerActivity.class));

        } else if (this.weight.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), ScaleActivity.class));

        } else if (this.sleep.equals(item.getDescription())) {
        } else if (this.heartRate.equals(item.getDescription())) {

            Intent intent = new Intent(getContext(), HeartRateActivity.class);
            intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
            intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
            startActivity(intent);

        } else {
        }
    }

    @Override
    public void onLongItemClick(View v, ItemGrid item) {
        throw new UnsupportedOperationException();
    }

//    Methods to manually add measurement button
    @Override
    public void onMenuContextClick(View v, ItemGrid item) {

        int type = item.getType();
        Intent it = new Intent(getContext(), ManuallyAddMeasurement.class);
        switch (type){
            case ItemGridType.ACTIVITY:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.ACTIVITY);
                startActivity(it);
                break;

            case ItemGridType.BLOOD_GLUCOSE:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.BLOOD_GLUCOSE);
                startActivity(it);
                break;

            case ItemGridType.BLOOD_PRESSURE:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;

            case ItemGridType.TEMPERATURE:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.TEMPERATURE);
                startActivity(it);
                break;

            case ItemGridType.WEIGHT:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.WEIGHT);
                startActivity(it);
                break;

            case ItemGridType.SLEEP:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.SLEEP);
                startActivity(it);
                break;

            case ItemGridType.HEART_RATE:
                it.putExtra(getResources().getString(R.string.measurementType),ItemGridType.HEART_RATE);
                startActivity(it);
                break;

            default:
                return;

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateDate(DateChangedEvent e){
        this.measurementsValues = e;

        updateGrid();
    }

}
