package br.edu.uepb.nutes.haniot.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.DeviceInforActivity;
import br.edu.uepb.nutes.haniot.adapter.BluetoothDeviceAdapter;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to scan BLE devices. The devices found are listed in a RecyclerView.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ScanDeviceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BluetoothDeviceAdapter.OnItemClickListener {
    private final String LOG = "ScanDeviceFragment";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final long SCAN_PERIOD = 10000; // 10s

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothGatt mGatt;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private List<BluetoothDevice> listBluetoothDevice;
    private ListAdapter adapterLeScanResult;

    @BindView(R.id.refresh_list_devices)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.list_devices_rv)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private Menu mMenu;

    public ScanDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_device, container, false);
        ButterKnife.bind(this, view);

        // Set a Toolbar to replace the ActionBar.
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.action_scanner_devices);

        mHandler = new Handler();

        // Verifica se o dispositivo suporta Bluetooth LE
        if (!ConnectionUtils.isSupportedBluetoothLE(getContext())) {
            Toast.makeText(getActivity(), R.string.not_bluetooth_le, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        initComponents();

        return view;
    }

    private void initComponents() {
        listBluetoothDevice = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BluetoothDeviceAdapter(listBluetoothDevice, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scan_device, menu);
        mMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan_stop:
                scanLeDevice(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            requestDependencies();
        } else {
            scanLeDevice(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            getActivity().finish();
            return;
        }
    }

    @Override
    public void onRefresh() {
        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        if (device == null) return;

        scanLeDevice(false);

        Intent it = new Intent(getActivity(), DeviceInforActivity.class);
        it.putExtra(DeviceInforActivity.EXTRAS_DEVICE_NAME, device.getName());
        it.putExtra(DeviceInforActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        startActivity(it);
    }

    /**
     * Aplica rotina para ativar bluetooth para qualquer versão e localização a partir do LOLLIPOP (21)
     */
    private void requestDependencies() {
        // Solicita ligação do bluetooth caso esteja desligado
        Intent enableBtItent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtItent, REQUEST_ENABLE_BLUETOOTH);

        // Verifica se o dispositivo possui Android >= 5
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getActivity(), R.string.permission_location, Toast.LENGTH_SHORT).show();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }
    }

    /**
     * Procura por dispositivos bluetooth ou encerra ao busca de acordo com o valor passado como parâmentro.
     *
     * @param enable true para buscar, false para parar a busca.
     */
    private void scanLeDevice(final boolean enable) {
        Log.i(LOG, "scanLeDevice()");

        if (!mBluetoothAdapter.isEnabled()) {
            requestDependencies();
        } else {
            if (enable) {
                mAdapter.notifyDataSetChanged();
                listBluetoothDevice.clear();
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                scanLeDeviceJellyBean(enable);
            } else {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                filters = new ArrayList<ScanFilter>();

                scanLeDeviceLollipop(enable);
            }
        }
    }


    /**
     * Rotina de busca para dispositivos abaixo do android versão 4.
     *
     * @param enable
     */
    private void scanLeDeviceJellyBean(final boolean enable) {
        final BluetoothAdapter.LeScanCallback mLeScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("onLeScan", rssi + "dbm");
                                addBluetoothDevice(device);
                            }
                        });
                    }
                };

        if (enable) {
            changeButtonScan(true);
            mSwipeRefreshLayout.setRefreshing(true);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeButtonScan(false);
                    mSwipeRefreshLayout.setRefreshing(false);

                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
        } else {
            changeButtonScan(false);
            mSwipeRefreshLayout.setRefreshing(false);

            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * Rotina de busca para dispositivos a partir do android versão 5.
     *
     * @param enable
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDeviceLollipop(final boolean enable) {
        final ScanCallback mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.i("callbackType", String.valueOf(callbackType));
                Log.i("result", result.toString());

                addBluetoothDevice(result.getDevice());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {
                    Log.i("ScanResult - Results", result.toString());
                    addBluetoothDevice(result.getDevice());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e("Scan Failed", "Error Code: " + errorCode);
            }
        };

        if (enable) {
            changeButtonScan(true);
            mSwipeRefreshLayout.setRefreshing(true);

            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeButtonScan(false);
                    mSwipeRefreshLayout.setRefreshing(false);

                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
        } else {
            changeButtonScan(false);
            mSwipeRefreshLayout.setRefreshing(false);

            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    private void changeButtonScan(boolean isScanning) {
        if (mMenu != null) {
            MenuItem menuStop = mMenu.findItem(R.id.action_scan_stop);

            if (menuStop != null) {
                if (isScanning) {
                    menuStop.setVisible(true);
                } else {
                    menuStop.setVisible(false);
                }
            }
        }
    }

    /**
     * Efetua conexão com o dispositivo
     *
     * @param device Dispositivo a ser conectado
     */
    private void connectToDevice(BluetoothDevice device) {
        Log.i(LOG, "connectToDevice()");

        mGatt = device.connectGatt(getActivity().getApplicationContext(), false, gattCallback);
        scanLeDevice(false);// will stop after first device detection
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };

    /**
     * Adiciona dispositivos encontrados na lista.
     *
     * @param device
     */
    private void addBluetoothDevice(BluetoothDevice device) {
        if (!listBluetoothDevice.contains(device)) {
            listBluetoothDevice.add(device);
            mAdapter.notifyDataSetChanged();
        }
    }

}
