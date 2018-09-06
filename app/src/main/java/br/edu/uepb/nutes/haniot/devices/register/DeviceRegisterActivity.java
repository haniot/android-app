package br.edu.uepb.nutes.haniot.devices.register;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceRegisterActivity extends AppCompatActivity implements DeviceProcessFragment.OnDeviceRegisterListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        ButterKnife.bind(this);

        initComponents();

        // TODO 1 - Pegar o device vindo da tela anterior
        replaceFragment(DeviceProcessFragment.newInstance());

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
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            transaction.replace(R.id.content, fragment).commit();
        }
    }
}
