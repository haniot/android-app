package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.fragment.AddWeightManuallyFragment;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManuallyAddMeasurement extends AppCompatActivity {

    private AddWeightManuallyFragment weightFragment;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_measurement);

        final Intent intent = getIntent();
        intent.getExtras();

        this.type = intent.getExtras().getInt("measurementType");
        if (type == -1){
            return;
        }else{
            if (ItemGridType.typeSupported(type)){
                replaceFragment(type);
            }else{
                return;
            }
        }

    }

    public void replaceFragment(int measurementType){
        switch (measurementType){
            case ItemGridType.WEIGHT:

                weightFragment = new AddWeightManuallyFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_add_measurement,
                        this.weightFragment)
                        .commit();

        }
    }

}
