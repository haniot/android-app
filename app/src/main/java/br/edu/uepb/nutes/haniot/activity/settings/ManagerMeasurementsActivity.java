package br.edu.uepb.nutes.haniot.activity.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.GridItemDeviceAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerMeasurementsActivity extends AppCompatActivity {

    @BindView(R.id.devices)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_measurement);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.monitor_measurements));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initResources();
    }

    public void initResources() {
        GridItemDeviceAdapter mAdapter = new GridItemDeviceAdapter(this);
        mAdapter.setHasStableIds(true);

        recyclerView.setAdapter(mAdapter);
        List<ItemDevice> itemDevices = new ArrayList<>();
        itemDevices.add(new ItemDevice(getString(R.string.key_blood_glucose),
                R.drawable.xglucosemeter,
                getString(R.string.glucose),
                getString(R.string.accu_check)));
        itemDevices.add(new ItemDevice(getString(R.string.key_activity),
                R.drawable.xrunning,
                getString(R.string.activity)));
        itemDevices.add(new ItemDevice(getString(R.string.key_blood_pressure),
                R.drawable.xblood_pressure,
                getString(R.string.blood_pressure)));
        itemDevices.add(new ItemDevice(getString(R.string.key_temperature),
                R.drawable.xtemperature,
                getString(R.string.temperature),
                getString(R.string.ear_thermometer_description)));
        itemDevices.add(new ItemDevice(getString(R.string.key_sleep),
                R.drawable.xsleep,
                getString(R.string.sleep)));
        itemDevices.add(new ItemDevice(getString(R.string.key_heart_rate),
                R.drawable.xcardiogram,
                getString(R.string.heart_rate),
                getString(R.string.heart_rate_sensor_h7)));
        itemDevices.add(new ItemDevice(getString(R.string.key_anthropometric),
                R.drawable.xshape,
                getString(R.string.anthropometric)));
        itemDevices.add(new ItemDevice(getString(R.string.key_weight)
                , R.drawable.xweight,
                getString(R.string.weight)));


        mAdapter.addItems(itemDevices);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);

        mAdapter.notifyDataSetChanged();
    }

    public class ItemDevice {
        private int image;
        private String title;
        private String device;
        private String key;

        public ItemDevice(String key, int image, String title) {
            this.key = key;
            this.image = image;
            this.title = title;
        }

        public ItemDevice(String key, int image, String title, String device) {
            this.key = key;
            this.image = image;
            this.title = title;
            this.device = device;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }
}
