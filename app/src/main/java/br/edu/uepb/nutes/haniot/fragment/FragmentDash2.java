package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.utils.GridSpacingItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDash2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDash2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDash2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //List of items that will be placed in grid;
    private List<Drawable> iconList      = new ArrayList<>();
    private List<String> descriptionList = new ArrayList<>();
    private List<String> nameList        = new ArrayList<>();

    private List<ItemGrid> buttonList    = new ArrayList<>();
    private SharedPreferences                          prefs;
    private SharedPreferences.Editor                  editor;
    private GridDashAdapter                          adapter;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    private FloatingActionButton newMeasurementButton;

    public FragmentDash2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDash2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDash2 newInstance(String param1, String param2) {
        FragmentDash2 fragment = new FragmentDash2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Cria lista de icones
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_pressure));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart2));
        iconList.add(getResources().getDrawable(R.drawable.ic_smartband));
        iconList.add(getResources().getDrawable(R.drawable.ic_ear_thermometer));
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_sugar));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_2));
        //Cria lista de nomes
        descriptionList.add(getResources().getString(R.string.blood_pressure_monitor));
        descriptionList.add(getResources().getString(R.string.heart_rate_sensor));
        descriptionList.add(getResources().getString(R.string.heart_rate_sensor));
        descriptionList.add(getResources().getString(R.string.smartband));
        descriptionList.add(getResources().getString(R.string.ear_thermometer));
        descriptionList.add(getResources().getString(R.string.accu_check));
        descriptionList.add(getResources().getString(R.string.body_composition1));
        descriptionList.add(getResources().getString(R.string.body_composition2));
        //Cria lista de descrições
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
    public void addButtomOnGrid(Drawable drawable, String description, String name){

        ItemGrid button = new ItemGrid(getContext(),getActivity());
        button.setIcon(drawable);
        button.setDescription(description);
        button.setName(name);
        buttonList.add(button);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash2, container, false);
        ButterKnife.bind(this,view);
        prefs = getContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        editor = prefs.edit();

        updateGrid();

        // Faz com que todos os itens tenham o mesmo tamanho
        gridMeasurement.setHasFixedSize(true);
        // Para itens de tamanhos iguais
        gridMeasurement.setLayoutManager(new GridLayoutManager(getContext(),calculateNoOfColumns(getContext())));
        //Para itens de diferentes tamanhos
       //gridMeasurement.setLayoutManager(new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()),StaggeredGridLayoutManager.VERTICAL));
        gridMeasurement.addItemDecoration(new GridSpacingItemDecoration(getContext(),R.dimen.item_dimen_dashboard));
        //Método utilizado para ajeitar o lag no scroll, o scroll utilizado é o do nested e nao da recyclerview
        gridMeasurement.setNestedScrollingEnabled(false);
        adapter = new GridDashAdapter(buttonList,getActivity());
        adapter.setHasStableIds(true);
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

    public void updateGrid(){
        //Limpa lista de botoes e a recyclerview
        buttonList.clear();
        gridMeasurement.removeAllViews();

        //Pega os dados que foram selecionados nas preferencias
        Boolean bloodPressureMonitor = prefs.getBoolean("blood_pressure_monitor",false);
        Boolean heartRateH10 = prefs.getBoolean("heart_rate_sensor_polar_h10",false);
        Boolean heartRateH7 = prefs.getBoolean("heart_rate_sensor_polar_h7",false);
        Boolean smartBand = prefs.getBoolean("smart_band",false);
        Boolean earThermometer = prefs.getBoolean("ear_thermometer",false);
        Boolean accuCheck = prefs.getBoolean("accu_check",false);
        Boolean bodyCompositionYunmai = prefs.getBoolean("body_composition_yunmai",false);
        Boolean bodyCompositionOmron = prefs.getBoolean("body_composition_omron",false);

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
        for(int i = 0; i < listaSwitchPressionados.size(); i++){
            if (listaSwitchPressionados.get(i)){
                addButtomOnGrid(iconList.get(i),descriptionList.get(i),nameList.get(i));
            }
        }
        adapter = new GridDashAdapter(buttonList,getActivity());
        adapter.setHasStableIds(true);
        gridMeasurement.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
