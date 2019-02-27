package br.edu.uepb.nutes.haniot.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.uepb.nutes.haniot.R;

import br.edu.uepb.nutes.haniot.model.SwipeEvent;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDashMain extends Fragment{

    @BindView(R.id.frame1)
    FrameLayout frame1;

    @BindView(R.id.frame2)
    FrameLayout frame2;

    @BindView(R.id.dash_swiperefresh)
    SwipeRefreshLayout swipe;

    private DashboardChartsFragment dashboardChartsFragment;
    private DashMeasurementsGridFragment fragmentDash2;

    private EventBus _eventBus;

    private final String TAG_FRAG1 = "fragment1";

    public FragmentDashMain() {}

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
//        date = dashboardChartsFragment.getData();
//        if (date == null || date.equals("")){
//            date = dashboardChartsFragment.getToday();
//        }
//        outState.putString("date",date);
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
        _eventBus = EventBus.getDefault();

        swipe.setOnRefreshListener(() -> {
            onSwipEvent();
        });

        return view;
    }

    @Subscribe
    public void onSwip(SwipeEvent event){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwipEvent(){

        EventBus.getDefault().post(new SwipeEvent());

        new Handler(getContext().getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                swipe.setRefreshing(false);
            }
        }, 1000);

    }

    @Override
    public void onStart() {
        super.onStart();
        _eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        _eventBus.unregister(this);
    }

}
