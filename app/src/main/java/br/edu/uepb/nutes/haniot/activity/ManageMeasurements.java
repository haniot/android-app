package br.edu.uepb.nutes.haniot.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.edu.uepb.nutes.haniot.R;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.adapter.ManageMeasurementAdapter;
import br.edu.uepb.nutes.haniot.model.ItemManageMeasurement;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageMeasurements extends AppCompatActivity {

    private List<Drawable> iconList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<SwitchCompat> switchCompatList = new ArrayList<>();
    private List<ItemManageMeasurement> itemList = new ArrayList<>();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerList)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_measurements);
        ButterKnife.bind(this);
        prefs = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        editor = prefs.edit();

        toolbar.setTitle(getResources().getString(R.string.manage_measurement_title));
        toolbar.setTitleTextColor(Color.WHITE);
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
        //Cria lista de icones
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_pressure_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_heart_64_2));
        iconList.add(getResources().getDrawable(R.drawable.ic_smartband_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_ear_thermometer_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_blood_sugar_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_64));
        iconList.add(getResources().getDrawable(R.drawable.ic_balance_2_64));

        //Cria lista de nomes
        nameList.add(getResources().getString(R.string.blood_pressure_monitor));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_h7));
        nameList.add(getResources().getString(R.string.heart_rate_sensor_h10));
        nameList.add(getResources().getString(R.string.smartband));
        nameList.add(getResources().getString(R.string.ear_thermometer));
        nameList.add(getResources().getString(R.string.accu_check));
        nameList.add(getResources().getString(R.string.body_composition_yunmai));
        nameList.add(getResources().getString(R.string.body_composition_monitor));

        //Essa parte cria os switchs com os respectivos Ids e os adiciona na lista de switchs
        Boolean bloodPressureMonitor = prefs.getBoolean("blood_pressure_monitor",false);
        Boolean heartRateH10 = prefs.getBoolean("heart_rate_sensor_polar_h10",false);
        Boolean heartRateH7 = prefs.getBoolean("heart_rate_sensor_polar_h7",false);
        Boolean smartBand = prefs.getBoolean("smart_band",false);
        Boolean earThermometer = prefs.getBoolean("ear_thermometer",false);
        Boolean accuCheck = prefs.getBoolean("accu_check",false);
        Boolean bodyCompositionYunmai = prefs.getBoolean("body_composition_yunmai",false);
        Boolean bodyCompositionOmron = prefs.getBoolean("body_composition_omron",false);
        SwitchCompat sw1 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw2 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw3 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw4 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw5 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw6 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw7 = new SwitchCompat(getApplicationContext());
        SwitchCompat sw8 = new SwitchCompat(getApplicationContext());
        sw1.setId(getResources().getInteger(R.integer.switch1));
        sw1.setChecked(bloodPressureMonitor);
        sw2.setId(getResources().getInteger(R.integer.switch2));
        sw2.setChecked(heartRateH10);
        sw3.setId(getResources().getInteger(R.integer.switch3));
        sw3.setChecked(heartRateH7);
        sw4.setId(getResources().getInteger(R.integer.switch4));
        sw4.setChecked(smartBand);
        sw5.setId(getResources().getInteger(R.integer.switch5));
        sw5.setChecked(earThermometer);
        sw6.setId(getResources().getInteger(R.integer.switch6));
        sw6.setChecked(accuCheck);
        sw7.setId(getResources().getInteger(R.integer.switch7));
        sw7.setChecked(bodyCompositionYunmai);
        sw8.setId(getResources().getInteger(R.integer.switch8));
        sw8.setChecked(bodyCompositionOmron);
        switchCompatList.add(sw1);
        switchCompatList.add(sw2);
        switchCompatList.add(sw3);
        switchCompatList.add(sw4);
        switchCompatList.add(sw5);
        switchCompatList.add(sw6);
        switchCompatList.add(sw7);
        switchCompatList.add(sw8);

        //Este for cria o objeto da lista e seta seus atributos com base nas listas acima
        for (int i=0; i< nameList.size(); i++){
            ItemManageMeasurement item = new ItemManageMeasurement(getApplicationContext());

            ImageView icon = new ImageView(getApplicationContext());
            icon.setImageDrawable(iconList.get(i));
            icon.setLayoutParams(new LinearLayout.LayoutParams(0,0));

            item.setIcon(icon);
            item.setName(nameList.get(i));
            item.setSwitchButton(switchCompatList.get(i));
            itemList.add(item);
        }

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
