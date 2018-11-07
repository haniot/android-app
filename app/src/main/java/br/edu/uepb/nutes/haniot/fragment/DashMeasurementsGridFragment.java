package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.model.DateEvent;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashMeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<ItemGrid>{

    //List of items that will be placed in grid;
    private List<Integer> iconList = new ArrayList<>();
    private List<String> descriptionList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Boolean> listaSwitchPressionados = new ArrayList<>();

    private List<ItemGrid> buttonList = new ArrayList<>();
    private Session session;
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

    private ArrayList<String> measurements = new ArrayList<>();
    private String measurementDate = "";

    private String deviceTypeTag;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    public DashMeasurementsGridFragment() {
    }

    public static DashMeasurementsGridFragment newInstance() {
        DashMeasurementsGridFragment fragment = new DashMeasurementsGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);

        PreferenceManager.setDefaultValues(
                getActivity(), R.xml.pref_manage_measurements, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Create the icon list of grid items
        iconList.add(R.drawable.ic_activity);
        iconList.add(R.drawable.ic_blood_glucose);
        iconList.add(R.drawable.ic_blood_pressure_64);
        iconList.add(R.drawable.ic_temperature);
        iconList.add(R.drawable.ic_weight);
        iconList.add(R.drawable.ic_sleeping);
        iconList.add(R.drawable.ic_heart_rate_64);
        //Create the name list of grid items
        descriptionList.add(getResources().getString(R.string.activity));
        descriptionList.add(getResources().getString(R.string.blood_glucose));
        descriptionList.add(getResources().getString(R.string.blood_pressure));
        descriptionList.add(getResources().getString(R.string.temperature));
        descriptionList.add(getResources().getString(R.string.weight));
        descriptionList.add(getResources().getString(R.string.sleep));
        descriptionList.add(getResources().getString(R.string.heart_rate));
        //Create the description list of grid items
        nameList.add(getResources().getString(R.string.blood_pressure_monitor_description));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_description_h7));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_description_h10));
        nameList.add(getResources().getString(R.string.smartband_description));
        nameList.add(getResources().getString(R.string.ear_thermometer_description));
        nameList.add(getResources().getString(R.string.accu_check_description));
        nameList.add(getResources().getString(R.string.body_composition_analyze_description));
        nameList.add(getResources().getString(R.string.body_composition_monitor_description));

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
        Log.d("TESTE","Initial date: "+this.measurementDate);
    }

    // Add button on the list of the grid
    public void addButtomOnGrid(Context context,
                                @DrawableRes int drawable,
                                String description,
                                String name,
                                String measurement,
                                int type) {

        ItemGrid button = new ItemGrid(context, drawable, description, name, measurement, type);
        buttonList.add(button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash2, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initComponents() {
        initRecyclerView();
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
    }

    private void getMeasurementsFromServer(){
        for (ItemGrid item : buttonList){
            Log.d("TESTE",item.getMeasurementInitials());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        initComponents();
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

    public void updateGrid() {
        //Limpa lista de botoes e a recyclerview
        buttonList.clear();
        gridMeasurement.removeAllViews();

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

//        //Adiciona na lista de itens selecionados
        this.listaSwitchPressionados.clear();
        listaSwitchPressionados.add(activity);
        listaSwitchPressionados.add(bloodGlucose);
        listaSwitchPressionados.add(bloodPressure);
        listaSwitchPressionados.add(temperature);
        listaSwitchPressionados.add(weight);
        listaSwitchPressionados.add(sleep);
        listaSwitchPressionados.add(heartRate);

        //Aqui a mágica acontece :D
        for (int i = 0; i < listaSwitchPressionados.size(); i++) {
            int itemType;
            if (listaSwitchPressionados.get(i)) {

                itemType = getInitials(descriptionList.get(i));

                if (itemType != -1) {
                    addButtomOnGrid(getContext(),
                            iconList.get(i),
                            descriptionList.get(i),
                            nameList.get(i),
                            "--",
                            itemType);
                }else{
                    return;
                }
            }
        }
//        getMeasurementsFromServer();
        mAdapter.clearItems();
        mAdapter.addItems(buttonList);
    }

    //Getting meassurement type, used to get measurement initials
    public int getInitials(String measurement){

        if (measurement.equals(activity)) return ItemGridType.ACTIVITY;
        else if (measurement.equals(glucose)) return ItemGridType.BLOOD_GLUCOSE;
        else if (measurement.equals(pressure)) return ItemGridType.BLOOD_PRESSURE;
        else if (measurement.equals(temperature)) return ItemGridType.TEMPERATURE;
        else if (measurement.equals(weight)) return ItemGridType.WEIGHT;
        else if (measurement.equals(sleep)) return ItemGridType.SLEEP;
        else if (measurement.equals(heartRate)) return ItemGridType.HEART_RATE;
        return -1;
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
    public void updateDate(DateEvent e){
        this.measurementDate = e.getDate();
        Log.d("TESTE","Recebi: "+e.getDate());
    }

}
