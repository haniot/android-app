package br.edu.uepb.nutes.haniot.elderly;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.ElderlyFallAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.Accessory;
import br.edu.uepb.nutes.haniot.model.DegreeEducationType;
import br.edu.uepb.nutes.haniot.model.Elderly;
import br.edu.uepb.nutes.haniot.model.Fall;
import br.edu.uepb.nutes.haniot.model.MaritalStatusType;
import br.edu.uepb.nutes.haniot.model.Medication;
import br.edu.uepb.nutes.haniot.model.dao.ElderlyDAO;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ElderlyPreviewActivity extends AppCompatActivity {
    public final String TAG = "ElderlyPreview";

    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.latest_falls_recyclerview)
    RecyclerView mLatestFallsRecyclerView;

    @BindView(R.id.elderly_pin_device_textView)
    TextView pinDeviceTextView;

    @BindView(R.id.elderly_weight_textView)
    TextView weightTextView;

    @BindView(R.id.elderly_height_textView)
    TextView heightTextView;

    @BindView(R.id.elderly_birth_textView)
    TextView dateOfBirthTextView;

    @BindView(R.id.elderly_sex_textView)
    TextView sexTextView;

    @BindView(R.id.elderly_phone_textView)
    TextView phoneTextView;


    @BindView(R.id.elderly_marital_status_textView)
    TextView maritalStatusTextView;

    @BindView(R.id.elderly_education_textView)
    TextView educationTextView;

    @BindView(R.id.elderly_live_alone_textView)
    TextView liveAloneTextView;

    @BindView(R.id.elderly_medications_textView)
    TextView medicationTextView;

    @BindView(R.id.elderly_accessories_textView)
    TextView accessoriesTextView;

    @BindView(R.id.no_data_textView)
    TextView noDataFallsTextView;

    @BindView(R.id.elderly_edit_floating_button)
    FloatingActionButton editButton;

    private Menu mMenu;
    private Elderly elderly;
    private ElderlyFallAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_preview);

        ButterKnife.bind(this);

        Intent it = getIntent();
        String elderlyId = it.getStringExtra(ElderlyPreviewActivity.EXTRA_ELDERLY_ID);
        if (elderlyId == null || elderlyId.isEmpty()) {
            Toast.makeText(this, R.string.elderly_was_not_select, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            elderly = ElderlyDAO.getInstance(this).get(elderlyId);
            Log.d(TAG,  "ID: " + elderlyId + "elderly: " + elderly);

//            elderly = new Elderly("Elvis da Silva Pereira", -595720800000L, 80.6D, 174, 0, 2, 1, false);
//            elderly.setPin("5874");
//            elderly.addMedication(new Medication("Dipirona"));
//            elderly.addAccessory(new Accessory(2, "Teste"));
//            elderly.addAccessory(new Accessory(0, "Teste 2"));
//            elderly.setPhone("83 981515-4454");
//            elderly.setFallRisk(2);
//
//            elderly.addFall(new Fall(1531083223781L, null));
//            elderly.addFall(new Fall(1431083238150L, null));

            initComponents();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drop_down_elderly_list, menu);

        if (elderly != null && elderly.getFallRisk() > 0)
            menu.getItem(0).setTitle(R.string.action_new_fall_risk_assessment);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_fall_risk:
                Intent intentFallRisk = new Intent(this, FallRiskActivity.class);
                intentFallRisk.putExtra(FallRiskActivity.EXTRA_ELDERLY_ID, elderly.get_id());
                startActivity(intentFallRisk);
                break;
            case R.id.action_fall_list:
                Intent intentFall = new Intent(this, ElderlyFallActivity.class);
                intentFall.putExtra(ElderlyFallActivity.EXTRA_ELDERLY_ID, elderly.get_id());
                startActivity(intentFall);
                break;
            case R.id.action_elderly_delete:
                Toast.makeText(getApplicationContext(), "action_elderly_delete ", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_elderly_delete_device_association:
                openDialogConfirmRemoveAssociation();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void initComponents() {
        initToolBar();
        populateFields();

        /**
         * Open form to edit elderly data.
         */
        editButton.setOnClickListener(v -> {
            Intent it = new Intent(this, ElderlyRegisterActivity.class);
            it.putExtra(ElderlyFormFragment.EXTRA_ELDERLY_PIN, elderly.getPin());
            it.putExtra(ElderlyFormFragment.EXTRA_ELDERLY_ID, elderly.get_id());
            startActivity(it);
            finish();
        });
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (elderly != null) mActionBar.setTitle(elderly.getName());
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
    }

    private void populateFields() {
        pinDeviceTextView.setText(elderly.getPin());
        weightTextView.setText(String.valueOf(elderly.getWeight()).concat("Kg"));
        heightTextView.setText(String.valueOf(elderly.getHeight()).concat("cm"));
        sexTextView.setText(elderly.getSex() == 0 ? getString(R.string.gender_male) :
                getString(R.string.gender_female));
        dateOfBirthTextView.setText(DateUtils.formatDate(elderly.getDateOfBirth(),
                getString(R.string.date_format)));
        phoneTextView.setText(elderly.getPhone());
        maritalStatusTextView.setText(MaritalStatusType.getString(this, elderly.getMaritalStatus()));
        educationTextView.setText(DegreeEducationType.getString(this, elderly.getDegreeOfEducation()));
        liveAloneTextView.setText(elderly.getLiveAlone() ? R.string.yes_text : R.string.no_text);

        /**
         * Setting medications
         */
        StringBuilder _strMed = new StringBuilder();
        boolean found = false;
        for (Medication m : elderly.getMedications()) {
            if (found) _strMed.append(", ");

            found = true;
            _strMed.append(m.getName());
        }
        if (_strMed.length() > 0) medicationTextView.setText(String.valueOf(_strMed));
        else medicationTextView.setText(R.string.elderly_not_medications);

        /**
         * Setting accessories
         */
        StringBuilder _strAcc = new StringBuilder();
        found = false;
        String[] _accessoriesArray = getResources().getStringArray(R.array.elderly_accessories_array);
        List<Accessory> _accessories = elderly.getAccessories();
        for (int i = 0; i < _accessories.size(); i++) {
            if (found) _strAcc.append(", ");

            found = true;
            if (_accessories.get(i).getId() < _accessoriesArray.length) {
                _strAcc.append(_accessoriesArray[i]);
            } else {
                _strAcc.append(_accessories.get(i).getName());
            }
        }
        if (_strAcc.length() != 0) accessoriesTextView.setText(String.valueOf(_strAcc));
        else accessoriesTextView.setText(R.string.elderly_not_accessories);

        // init recyclerview falls
        initFallsRecyclerView();
    }

    private void initFallsRecyclerView() {
        mAdapter = new ElderlyFallAdapter(this);
        mLatestFallsRecyclerView.setHasFixedSize(true);
        mLatestFallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLatestFallsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLatestFallsRecyclerView.setAdapter(mAdapter);
        if (elderly.getFalls().size() > 0)
            mAdapter.addItems(elderly.getFalls());
        else noDataFallsTextView.setVisibility(View.VISIBLE);


        mAdapter.setListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(Object item) {

            }

            @Override
            public void onLongItemClick(View v, Object item) {

            }

            @Override
            public void onMenuContextClick(View v, Object item) {

            }
        });
    }

    /**
     * Dialog confirmation to remove association with device.
     */
    private void openDialogConfirmRemoveAssociation() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setTitle(R.string.warning_title)
                    .setMessage(getResources().getString(R.string.elderly_confirm_delete_device_association, elderly.getName()))
                    .setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                        // TODO Relizar rotina para remover o pin/atualizar view/atualizar elderly local e remoto.
                    })
                    .setNegativeButton(R.string.no_text, null)
                    .create().show();
        });
    }
}
