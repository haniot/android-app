package br.edu.uepb.nutes.haniot.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.edu.uepb.nutes.haniot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDashMain extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.frame1)
    FrameLayout frame1;

    @BindView(R.id.frame2)
    FrameLayout frame2;

    private DashboardCharts dashboardCharts;
    private DashboardDevicesGrid fragmentDash2;

    private String dateFrag1;
    private final String TAG_FRAG1 = "fragment1";

    public FragmentDashMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDashMain.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDashMain newInstance(String param1, String param2) {
        FragmentDashMain fragment = new FragmentDashMain();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardCharts = new DashboardCharts();
        if (savedInstanceState != null){
            Bundle bundle = new Bundle();
            bundle.putString("date",savedInstanceState.getString("date"));
            dashboardCharts.setArguments(bundle);
        }
        fragmentDash2 = new DashboardDevicesGrid();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String date;
        date = dashboardCharts.getData();
        if (date == null || date.equals("")){
            date = dashboardCharts.getToday();
        }
        outState.putString("date",date);
        dashboardCharts = new DashboardCharts();
        dashboardCharts.setArguments(outState);
        if (dashboardCharts.isAdded()) {
            getFragmentManager().putFragment(outState, TAG_FRAG1, dashboardCharts);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash_main, container, false);
        ButterKnife.bind(this,view);

        //Addicting the two main fragmants to dashboard

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.frame1, this.dashboardCharts, this.TAG_FRAG1)
                    .commit();
        }

        getFragmentManager().beginTransaction().replace(R.id.frame2, this.fragmentDash2, fragmentDash2.getTag())
                 .commit();
        return view;
    }

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
