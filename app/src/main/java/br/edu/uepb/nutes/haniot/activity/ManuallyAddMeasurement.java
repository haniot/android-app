package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.fragment.AddWeightManuallyFragment;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManuallyAddMeasurement extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    Fragment to be replaced
    private Fragment myFragment;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_measurement);
        ButterKnife.bind(this);

//        Default methods for toolbar, remember of use themes on layout xml
        toolbar.setTitle(getResources().getString(R.string.manually_add_measurement));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        intent.getExtras();

//        get the type of measurement to replace the fragment
        this.type = intent.getExtras().getInt(getResources().getString(R.string.measurementType));
        if (type == -1){
            finish();
        }else{
            if (ItemGridType.typeSupported(type)){
                replaceFragment(type);
            }else{
                finish();
            }
        }

    }

//    Here the fragment of date is replaced first and the measurement fragment after for each
//    measurement
    public void replaceFragment(int measurementType){
        switch (measurementType){
            case ItemGridType.WEIGHT:

                myFragment = new AddWeightManuallyFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_date,
                        myFragment)
                        .commit();
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
