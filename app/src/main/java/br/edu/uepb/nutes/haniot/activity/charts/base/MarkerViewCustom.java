package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import br.edu.uepb.nutes.haniot.R;

/**
 * Constructor of chart.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class MarkerViewCustom  extends MarkerView {

    private TextView tvContent;
    private RelativeLayout layoutBackground;

    public MarkerViewCustom(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        layoutBackground = (RelativeLayout) findViewById(R.id.marker_background);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public TextView getTvContent() {
        return tvContent;
    }



    public RelativeLayout getLayoutBackground() {
        return layoutBackground;
    }



    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText("" + e.getY());

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}
