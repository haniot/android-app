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

import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDashMain extends Fragment implements DashboardChartsFragment.SendDateListener{

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.frame1)
    FrameLayout frame1;

    @BindView(R.id.frame2)
    FrameLayout frame2;

    private DashboardChartsFragment dashboardChartsFragment;
    private DashMeasurementsGridFragment fragmentDash2;

    private String dateFrag1;
    private final String TAG_FRAG1 = "fragment1";

    public FragmentDashMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardChartsFragment = new DashboardChartsFragment();
        if (savedInstanceState != null){
            Bundle bundle = new Bundle();
            bundle.putString("date",savedInstanceState.getString("date"));
            dashboardChartsFragment.setArguments(bundle);
        }
        fragmentDash2 = new DashMeasurementsGridFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String date;
        date = dashboardChartsFragment.getData();
        if (date == null || date.equals("")){
            date = dashboardChartsFragment.getToday();
        }
        outState.putString("date",date);
        dashboardChartsFragment = new DashboardChartsFragment();
        dashboardChartsFragment.setArguments(outState);
        if (dashboardChartsFragment.isAdded()) {
            getFragmentManager().putFragment(outState, TAG_FRAG1, dashboardChartsFragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash_main, container, false);
        ButterKnife.bind(this,view);

        //Addicting the two main fragmants to dashboard

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.frame1, this.dashboardChartsFragment, this.TAG_FRAG1)
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

    @Override
    public void onSendDate(String date) {
        Log.d("TESTE","Data: "+date);
        fragmentDash2 = new DashMeasurementsGridFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date",date);

        fragmentDash2.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame2, this.fragmentDash2, fragmentDash2.getTag())
                .commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
