package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;

/**
 * Created by Fabio on 10/05/2018.
 */

public class InfoAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<InfoMeasurement> infoMeasurements;

        // 1
        public InfoAdapter(Context context, ArrayList<InfoMeasurement> infoMeasurements) {
            this.mContext = context;
            this.infoMeasurements = infoMeasurements;
        }

        // 2
        @Override
        public int getCount() {
            return infoMeasurements.size();
        }

        // 3
        @Override
        public long getItemId(int position) {
            return 0;
        }

        // 4
        @Override
        public Object getItem(int position) {
            return null;
        }

        // 5
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 1
            final InfoMeasurement info = infoMeasurements.get(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.item_info_measurement, null);
            }

            // 3
            final TextView title = convertView.findViewById(R.id.title_info);
            final TextView value = (TextView)convertView.findViewById(R.id.value_info);

            // 4
            title.setText(info.getTitle());
            value.setText(info.getValue());

            if (info.getRisk() == InfoMeasurement.Risk.Up) value.setTextColor(Color.RED);
            if (info.getRisk() == InfoMeasurement.Risk.Down) value.setTextColor(Color.BLUE);
            if (info.getTitle() == convertView.getResources().getString(R.string.info_period)) value.setTextSize(18);

            return convertView;
        }

    }