package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import br.edu.uepb.nutes.haniot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDashMain extends Fragment{

    @BindView(R.id.frameCharts)
    FrameLayout frameChart;

    @BindView(R.id.frameMeasurements)
    FrameLayout frameMeasurements;


    private DashboardChartsFragment chartsFragment;
    private MeasurementsGridFragment measurementsFragment;

    private EventBus _eventBus;

    private final String TAG_FRAG1 = "fragment1";

    public FragmentDashMain() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chartsFragment = new DashboardChartsFragment();
        if (savedInstanceState != null){
            Bundle bundle = new Bundle();
            bundle.putString("date",savedInstanceState.getString("date"));
            chartsFragment.setArguments(bundle);
        }
        measurementsFragment = new MeasurementsGridFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String date;
//        date = chartsFragment.getData();
//        if (date == null || date.equals("")){
//            date = chartsFragment.getToday();
//        }
//        outState.putString("date",date);
        chartsFragment = new DashboardChartsFragment();
        chartsFragment.setArguments(outState);
        if (chartsFragment.isAdded()) {
            getFragmentManager().putFragment(outState, TAG_FRAG1, chartsFragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash_main, container, false);
        ButterKnife.bind(this,view);

        //Addicting the two main fragmants to dashboard

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.frameCharts, this.chartsFragment, this.TAG_FRAG1)
                    .commit();
        }

        getFragmentManager().beginTransaction().replace(R.id.frameMeasurements, this.measurementsFragment, measurementsFragment.getTag())
                 .commit();


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
