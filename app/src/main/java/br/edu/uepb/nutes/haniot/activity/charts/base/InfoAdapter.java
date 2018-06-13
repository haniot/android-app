package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;

/**
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 *
 * **/

public class InfoAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<InfoMeasurement> infoMeasurements;

    public InfoAdapter(Context context, ArrayList<InfoMeasurement> infoMeasurements) {
        this.mContext = context;
        this.infoMeasurements = infoMeasurements;
    }

    @Override
    public int getCount() {
        return infoMeasurements.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InfoMeasurement info = infoMeasurements.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_info_measurement, null);
        }

        final TextView title = convertView.findViewById(R.id.title_info);
        final TextView value = (TextView) convertView.findViewById(R.id.value_info);
        title.setText(info.getTitle());

        if (info.getTitle().equals(convertView.getResources().getString(R.string.info_period))) value.setTextSize(18);

        value.setText(info.getValue());

        return convertView;
    }

}