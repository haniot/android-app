package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.hdp.BodyCompositionHDPActivity;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.utils.GridSpacingItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardDevicesGrid extends Fragment {

    private OnFragmentInteractionListener mListener;

    //List of items that will be placed in grid;
    private List<Drawable> iconList = new ArrayList<>();
    private List<String> descriptionList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();

    private List<ItemGrid> buttonList = new ArrayList<>();
    private Session session;
    private GridDashAdapter adapter;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    public DashboardDevicesGrid() {
    }

    public static DashboardDevicesGrid newInstance(String param1, String param2) {
        DashboardDevicesGrid fragment = new DashboardDevicesGrid();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create the icon list of grid items
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_pressure));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart2));
        iconList.add(getResources().getDrawable(R.drawable.ic_smartband));
        iconList.add(getResources().getDrawable(R.drawable.ic_ear_thermometer));
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_sugar));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_2));
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
    public void addButtomOnGrid(Drawable drawable, String description, String name) {

        ItemGrid button = new ItemGrid(getContext());
        button.setIcon(drawable);
        button.setDescription(description);
        button.setName(name);
        buttonList.add(button);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash2, container, false);
        ButterKnife.bind(this, view);
        session = new Session(getContext());

        updateGrid();
        // This method set the same size to all items of grid
        gridMeasurement.setHasFixedSize(true);
        // Set a grid layout to recyclerview, the calculateNoOfColumns was used to set the grid autospacing
        gridMeasurement.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(getContext())));
        gridMeasurement.addItemDecoration(new GridSpacingItemDecoration(getContext(), R.dimen.item_dimen_dashboard));

        //Method used to fix lag on scroll in recyclerview
        gridMeasurement.setNestedScrollingEnabled(false);

        adapter = new GridDashAdapter(getContext());
        adapter.setHasStableIds(true);
        adapter.setListener(new OnRecyclerViewListener<ItemGrid>() {

            @Override
            public void onItemClick(ItemGrid item) {
                System.out.println("==== teste");
            }
        });
        adapter.addItems(buttonList);
        gridMeasurement.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        updateGrid();
        super.onResume();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 260);
        return noOfColumns;
    }

    public void updateGrid() {
        //Limpa lista de botoes e a recyclerview
        buttonList.clear();
        gridMeasurement.removeAllViews();

        //Pega os dados que foram selecionados nas preferencias
        Boolean bloodPressureMonitor = session.getBoolean(getResources().getString(R.string.blood_pressure_monitor_pref));
        Boolean heartRateH10 = session.getBoolean(getResources().getString(R.string.heart_rate_sensor_polar_h10_pref));
        Boolean heartRateH7 = session.getBoolean(getResources().getString(R.string.heart_rate_sensor_polar_h7_pref));
        Boolean smartBand = session.getBoolean(getResources().getString(R.string.smart_band_pref));
        Boolean earThermometer = session.getBoolean(getResources().getString(R.string.ear_thermometer_pref));
        Boolean accuCheck = session.getBoolean(getResources().getString(R.string.accu_check_pref));
        Boolean bodyCompositionYunmai = session.getBoolean(getResources().getString(R.string.body_composition_yunmai_pref));
        Boolean bodyCompositionOmron = session.getBoolean(getResources().getString(R.string.body_composition_omron_pref));

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
        adapter = new GridDashAdapter(getContext());
        adapter.addItems(buttonList);
        adapter.setHasStableIds(true);
        gridMeasurement.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
