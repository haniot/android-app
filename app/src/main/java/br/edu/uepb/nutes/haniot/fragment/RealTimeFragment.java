package br.edu.uepb.nutes.haniot.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.LineDataSet;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.base.CreateChart;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Measurement;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fabio on 02/05/2018.
 */

public class RealTimeFragment extends Fragment {

    @BindView(R.id.real_time_chart)
    public Chart lineChart;
    private CreateChart mChart;
    static private Context mContext;

    public RealTimeFragment() {
    }

    public static RealTimeFragment newInstance(Context context) {
        RealTimeFragment fragment = new RealTimeFragment();
        mContext = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_linechart, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public void sendMeasurement(Measurement measurement){
        mChart.paint(measurement);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChart = new CreateChart.Params(mContext, lineChart)
                .drawValues(false)
                .yAxisEnabled(false)
                .xAxisEnabled(false)
                .drawValues(false)
                .colorFontDescription(Color.WHITE)
                .setMaxVisibility(20,20)
                .setStyleFilledLine(true, getResources().getColor(R.color.colorAccent))
                .setTypeLine(LineDataSet.Mode.CUBIC_BEZIER)
                .colorGridChart(0,0)
                .drawCircleStyle(0,0)
                .build();
    }
}
