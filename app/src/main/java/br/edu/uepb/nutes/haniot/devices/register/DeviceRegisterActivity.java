package br.edu.uepb.nutes.haniot.devices.register;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceRegisterActivity extends AppCompatActivity
        implements DeviceProcessFragment.OnDeviceRegisterListener {
    private final String TAG = "DeviceRegisterActivity ";
    public final static String EXTRA_DEVICE = "extra_device";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        ButterKnife.bind(this);

        initComponents();

        // set arguments in fragments DeviceProcessFragment
        DeviceProcessFragment deviceProcessFragment = DeviceProcessFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DEVICE, getIntent().getParcelableExtra(EXTRA_DEVICE));
        deviceProcessFragment.setArguments(bundle);

        // open fragments default DeviceProcessFragment
        replaceFragment(deviceProcessFragment);
    }

    /**
     * Initialize the components.
     */
    private void initComponents() {
        initToolBar();
    }

    /**
     * Initialize toolbar and insert title.
     */
    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.devices));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.content, fragment).commit();
        }
    }

    @Override
    public void onClickStartScan(Device device) {
        // Replace no fragments DeviceScannerFragment
    }

}
