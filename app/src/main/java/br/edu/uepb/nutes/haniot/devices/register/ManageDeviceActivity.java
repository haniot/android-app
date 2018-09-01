package br.edu.uepb.nutes.haniot.devices.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;

public class ManageDeviceActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);

        mRecyclerView = findViewById(R.id.lista_desvices);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ManageDeviceAdapter(getListDevices(8));
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public List<Device> getListDevices(int quant) {
        String[] nameDevices = new String[]{"Ear Thermometer", "Accu-Check", "Smart Band2", "Heart Heate Sensor",
                "Heart Heate Sensor", "Body Composition Monitor", "Body Composition Analysis", "Blood Pressure Monitor"};

        String[] model = new String[]{"DL8740", "Performa Connect", "MI BAND 2", "Polar H7", "Polar H10", "OMRON HBF-206IT",
                "YUNMAI Mini 1501", "OMRON BPT792IT"};

        int imagesDevices[] = new int[]{R.drawable.img_thermometer, R.drawable.img_accu_chek,
                R.drawable.img_smart_band2, R.drawable.img_pollarh7, R.drawable.img_pollarh10,
                R.drawable.img_balance_onrom, R.drawable.img_balance_yunmai, R.drawable.img_blood_pressure};

        List<Device> listAuxDevices = new ArrayList<>();
        for (int i = 0; i < quant; i++) {
            Device d = new Device(nameDevices[i % nameDevices.length], model[i % model.length], imagesDevices[i % nameDevices.length]);
            listAuxDevices.add(d);
        }
        return listAuxDevices;
    }
}
