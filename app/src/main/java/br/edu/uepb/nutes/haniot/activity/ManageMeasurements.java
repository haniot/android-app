package br.edu.uepb.nutes.haniot.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.adapter.ManageMeasurementAdapter;
import br.edu.uepb.nutes.haniot.model.ItemManageMeasurement;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageMeasurements extends AppCompatActivity {

//    private List<LinearLayout> itemList = new ArrayList<>();
    private List<Drawable> iconList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Integer> idList = new ArrayList<>();
    private List<ItemManageMeasurement> itemList = new ArrayList<>();
    private View horizontalDivider;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerList)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_measurements);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.manage_measurement_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initList();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ManageMeasurementAdapter(itemList,getApplicationContext()));

    }

    public void initList(){
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_pressure_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart_64_2));
        iconList.add(getResources().getDrawable(R.drawable.ic_smartband_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_ear_thermometer_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_sugar_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_2_64));

        nameList.add(getResources().getString(R.string.blood_pressure_monitor));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_h10));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_h7));
        nameList.add(getResources().getString(R.string.smartband));
        nameList.add(getResources().getString(R.string.ear_thermometer));
        nameList.add(getResources().getString(R.string.accu_check));
        nameList.add(getResources().getString(R.string.body_composition_yunmai));
        nameList.add(getResources().getString(R.string.body_composition_monitor));

        for (int i=0; i< nameList.size(); i++){
            ItemManageMeasurement item = new ItemManageMeasurement(getApplicationContext());

            ImageView icon = new ImageView(getApplicationContext());
            icon.setImageDrawable(iconList.get(i));
            icon.setLayoutParams(new LinearLayout.LayoutParams(0,0));

            SwitchCompat switchButton = new SwitchCompat(getApplicationContext());

            item.setIcon(icon);
            item.setName(nameList.get(i));
            item.setSwitchButton(switchButton);
            itemList.add(item);
        }

    }

    public LinearLayout addItemsOnList(ImageView icon,String text){
        LinearLayout linha = new LinearLayout(getApplicationContext());
        linha.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linha.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iconList = icon;
        iconList.setLayoutParams(new LinearLayout.LayoutParams(0, 80,1));
        iconList.setId(View.generateViewId());

        TextView textMeasurement = new TextView(getApplicationContext());
        textMeasurement.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,7));
        textMeasurement.setGravity(Gravity.CENTER_VERTICAL);
        textMeasurement.setPaddingRelative(5,0,0,0);
        textMeasurement.setText(text);
        textMeasurement.setTextColor(Color.BLACK);
        textMeasurement.setId(View.generateViewId());

        Switch switchButton = new Switch(getApplicationContext());
        switchButton.setId(View.generateViewId());

        linha.addView(iconList);
        linha.addView(textMeasurement);
        linha.addView(switchButton);

        return linha;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
