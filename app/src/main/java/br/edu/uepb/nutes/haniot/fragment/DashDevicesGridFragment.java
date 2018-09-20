package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.SmartBandActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BodyCompositionHDPActivity;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.utils.GridSpacingItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashDevicesGridFragment extends Fragment implements OnRecyclerViewListener<ItemGrid> {

    //List of items that will be placed in grid;
    private List<Integer> iconList = new ArrayList<>();
    private List<String> descriptionList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();

    private List<ItemGrid> buttonList = new ArrayList<>();
    private Session session;
    private Context mContext;
    private GridDashAdapter mAdapter;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    public DashDevicesGridFragment() {
    }

    public static DashDevicesGridFragment newInstance() {
        DashDevicesGridFragment fragment = new DashDevicesGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);

        //Create the icon list of grid items
        iconList.add(R.drawable.ic_blood_pressure);
        iconList.add(R.drawable.ic_heart);
        iconList.add(R.drawable.ic_heart2);
        iconList.add(R.drawable.ic_smartband);
        iconList.add(R.drawable.ic_ear_thermometer);
        iconList.add(R.drawable.ic_blood_sugar);
        iconList.add(R.drawable.ic_balance);
        iconList.add(R.drawable.ic_balance_2);
        //Create the name list of grid items
        descriptionList.add(getResources().getString(R.string.blood_pressure_monitor));
        descriptionList.add(getResources().getString(R.string.heart_rate_sensor));
        descriptionList.add(getResources().getString(R.string.heart_rate_sensor));
        descriptionList.add(getResources().getString(R.string.smartband));
        descriptionList.add(getResources().getString(R.string.ear_thermometer));
        descriptionList.add(getResources().getString(R.string.accu_check));
        descriptionList.add(getResources().getString(R.string.body_composition1));
        descriptionList.add(getResources().getString(R.string.body_composition2));
        //Create the description list of grid items
        nameList.add(getResources().getString(R.string.blood_pressure_monitor_description));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_description_h7));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_description_h10));
        nameList.add(getResources().getString(R.string.smartband_description));
        nameList.add(getResources().getString(R.string.ear_thermometer_description));
        nameList.add(getResources().getString(R.string.accu_check_description));
        nameList.add(getResources().getString(R.string.body_composition_analyze_description));
        nameList.add(getResources().getString(R.string.body_composition_monitor_description));
    }

    // Add button on the list of the grid
    public void addButtomOnGrid(@DrawableRes int drawable, String description, String name) {
        ItemGrid button = new ItemGrid(drawable, description, name);
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
        initComponents();
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
        /**
         * Set a grid layout to recyclerview,
         * the calculateNoOfColumns was used to set the grid autospacing
         */
        gridMeasurement.setLayoutManager(new GridLayoutManager(mContext,
                calculateNoOfColumns(mContext)));
        gridMeasurement.setItemAnimator(new DefaultItemAnimator());
        gridMeasurement.addItemDecoration(new GridSpacingItemDecoration(mContext,
                R.dimen.item_dimen_dashboard));
        //Method used to fix lag on scroll in recyclerview
        gridMeasurement.setNestedScrollingEnabled(false);

        mAdapter.setListener(this);
        gridMeasurement.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGrid();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 260);
        return noOfColumns;
    }

    public void updateGrid() {
        Log.w("TEST", "updateGrid()");
        //Limpa lista de botoes e a recyclerview
        buttonList.clear();
        gridMeasurement.removeAllViews();

        //Pega os dados que foram selecionados nas preferencias
        Boolean bloodPressureMonitor = session.getBoolean(getResources()
                .getString(R.string.blood_pressure_monitor_pref));
        Boolean heartRateH10 = session.getBoolean(getResources()
                .getString(R.string.heart_rate_sensor_polar_h10_pref));
        Boolean heartRateH7 = session.getBoolean(getResources()
                .getString(R.string.heart_rate_sensor_polar_h7_pref));
        Boolean smartBand = session.getBoolean(getResources()
                .getString(R.string.smart_band_pref));
        Boolean earThermometer = session.getBoolean(getResources()
                .getString(R.string.ear_thermometer_pref));
        Boolean accuCheck = session.getBoolean(getResources()
                .getString(R.string.accu_check_pref));
        Boolean bodyCompositionYunmai = session.getBoolean(getResources()
                .getString(R.string.body_composition_yunmai_pref));
        Boolean bodyCompositionOmron = session.getBoolean(getResources()
                .getString(R.string.body_composition_omron_pref));

        //Adiciona na lista de itens selecionados
        List<Boolean> listaSwitchPressionados = new ArrayList<>();
        listaSwitchPressionados.add(bloodPressureMonitor);
        listaSwitchPressionados.add(heartRateH10);
        listaSwitchPressionados.add(heartRateH7);
        listaSwitchPressionados.add(smartBand);
        listaSwitchPressionados.add(earThermometer);
        listaSwitchPressionados.add(accuCheck);
        listaSwitchPressionados.add(bodyCompositionYunmai);
        listaSwitchPressionados.add(bodyCompositionOmron);

        //Aqui a mágica acontece :D
        for (int i = 0; i < listaSwitchPressionados.size(); i++) {
            if (listaSwitchPressionados.get(i)) {
                addButtomOnGrid(iconList.get(i), descriptionList.get(i), nameList.get(i));
            }
        }
        mAdapter.clearItems();
        mAdapter.addItems(buttonList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(ItemGrid item) {
        switch (item.getName()){
            case "YUNMAI Mini 1501":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),BodyCompositionHDPActivity.class));

                    }
                }, 200);
                break;

            case "DL8740":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),ThermometerActivity.class));

                    }
                }, 200);
                break;

            case "OMRON BP792IT":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),BloodPressureHDPActivity.class));

                    }
                }, 200);
                break;

            case "Polar H7":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        Intent it = new Intent(getContext(),HeartRateActivity.class);
                        it.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "00:22:D0:BA:95:80");
                        it.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H7"});
                        startActivity(it);

                    }
                }, 200);
                break;

            case "Polar H10":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        Intent intent = new Intent(getContext(),HeartRateActivity.class);
                        intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
                        intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
                        startActivity(intent);

                    }
                }, 200);
                break;

            case "MI BAND 2":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),SmartBandActivity.class));

                    }
                }, 200);
                break;

            case "Performa Connect":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),GlucoseActivity.class));

                    }
                }, 200);
                break;

            case "OMRON HBF-206ITH":

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // do something...
                        startActivity(new Intent(getContext(),ScaleActivity.class));

                    }
                }, 200);
                break;

            default:
                break;
        }
    }

    @Override
    public void onLongItemClick(View v, ItemGrid item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onMenuContextClick(View v, ItemGrid item) {
        throw new UnsupportedOperationException();
    }
}
