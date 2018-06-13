package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.charts.BodyCompositionChartActivity;
import br.edu.uepb.nutes.haniot.activity.charts.TemperatureChartActivity;

/**
 * Created by Fabio on 04/06/2018.
 */

public class ValueFormatter implements IValueFormatter {

    private Context context;
    private DecimalFormat mFormat;

    public ValueFormatter(Context context) {
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if(context instanceof BodyCompositionChartActivity ||  context instanceof TemperatureChartActivity) {
            mFormat = new DecimalFormat(context.getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));
            return mFormat.format(value);
        }
        return String.format("%03d", (int) value);
    }
}

