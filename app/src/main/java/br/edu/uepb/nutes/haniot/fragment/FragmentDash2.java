package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
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
    private List<ItemGrid> buttonList = new ArrayList<>();

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

        Drawable img1 = getResources().getDrawable(R.drawable.ic_balance);
        addButtomOnGrid(img1,getResources().getString(R.string.body_composition1), "YUNMAI Mini 1501");
        Drawable img2 = getResources().getDrawable(R.drawable.ic_ear_thermometer);
        addButtomOnGrid(img2,getResources().getString(R.string.ear_thermometer),"DL8740");
        Drawable img3 = getResources().getDrawable(R.drawable.ic_heart);
        addButtomOnGrid(img3,getResources().getString(R.string.heart_rate_sensor),"Polar H10");
        Drawable img4 = getResources().getDrawable(R.drawable.ic_heart2);
        addButtomOnGrid(img4,getResources().getString(R.string.heart_rate_sensor),"Polar H7");
        Drawable img5 = getResources().getDrawable(R.drawable.ic_smartband);
        addButtomOnGrid(img5,getResources().getString(R.string.smartband),"MI BAND 2");
        Drawable img6 = getResources().getDrawable(R.drawable.ic_blood_pressure);
        addButtomOnGrid(img6,getResources().getString(R.string.blood_pressure_monitor),"OMRON BP792IT");
        Drawable img7 = getResources().getDrawable(R.drawable.ic_blood_sugar);
        addButtomOnGrid(img7,getResources().getString(R.string.accu_check),"Performa Connect");
        Drawable img8 = getResources().getDrawable(R.drawable.ic_balance_2);
        addButtomOnGrid(img8,getResources().getString(R.string.body_composition2),"OMRON HBF-206ITH");

        // Faz com que todos os itens tenham o mesmo tamanho
        gridMeasurement.setHasFixedSize(true);
        // Para itens de tamanhos iguais
        gridMeasurement.setLayoutManager(new GridLayoutManager(getContext(),calculateNoOfColumns(getContext())));
        //Para itens de diferentes tamanhos
       //gridMeasurement.setLayoutManager(new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()),StaggeredGridLayoutManager.VERTICAL));
        gridMeasurement.addItemDecoration(new GridSpacingItemDecoration(getContext(),R.dimen.item_dimen_dashboard));
        //Método utilizado para ajeitar o lag no scroll, o scroll utilizado é o do nested e nao da recyclerview
        gridMeasurement.setNestedScrollingEnabled(false);
        GridDashAdapter adapter = new GridDashAdapter(buttonList);
        adapter.setHasStableIds(true);
        gridMeasurement.setAdapter(adapter);

        return view;
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 260);
        return noOfColumns;
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
