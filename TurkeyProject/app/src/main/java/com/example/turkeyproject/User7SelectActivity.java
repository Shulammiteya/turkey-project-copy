package com.example.turkeyproject;;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class User7SelectActivity extends AppCompatActivity {
    private static String TAG = "ConnectActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean mScanning,permission;
    private Button scan;
    private Button directStart;
    private ListView ble_list;
    private BLEDevices bleDevices;
    private ListAdapter scannedDeviceAdapter;
    private Handler mHandler;
    private Button connectButton;
    private static final int LOCATION_REQUEST_CODE = 2;

    private String PatientName, DoctorName, strength;

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user7_select);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");
        strength = getIntent().getStringExtra("strength");

        objInit();
        getPermissions();
        bleInit();
        setListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOCATION_REQUEST_CODE){
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    protected void objInit() {
        scan = findViewById(R.id.btnScan);
        ble_list = findViewById(R.id.lstBle);
        connectButton = findViewById(R.id.btnConnect);
        directStart = findViewById(R.id.btnDirectStart);

        bleDevices = new BLEDevices();
        mHandler = new Handler();
        scannedDeviceAdapter = new ListAdapter(this, bleDevices.getNames());
        ble_list.setAdapter(scannedDeviceAdapter);
    }

    protected void getPermissions() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(User7SelectActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        int readPermissions = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermissions = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordPermissions = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(writePermissions != PackageManager.PERMISSION_GRANTED || readPermissions != PackageManager.PERMISSION_GRANTED || recordPermissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(User7SelectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    protected void bleInit() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    protected void setListeners() {
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializes list view adapter.
                //scannedAddressAdapter.clear();
                scanLeDevice(true);
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User7SelectActivity.this, User4TrainActivity.class);

                intent.putExtra("pName", PatientName);
                intent.putExtra("dName", DoctorName);
                intent.putExtra("strength", strength);

                intent.putExtra("blemode", 1);
                intent.putExtra("devicesToConnect", bleDevices.getSelectedBluetoothDevices());
                startActivity(intent);
                finish();
            }
        });
        directStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User7SelectActivity.this, User4TrainActivity.class);

                intent.putExtra("pName", PatientName);
                intent.putExtra("dName", DoctorName);
                intent.putExtra("strength", strength);

                intent.putExtra("blemode", 0);
                startActivity(intent);
                finish();
            }
        });
        ble_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bleDevices.selectDevice(position);
                CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.check);
                chkItem.setChecked(bleDevices.isDeviceSelected(position));
                bleDevices.getSelectedBluetoothDevices();
            }
        });
    }

    private void scanLeDevice(final boolean enable) {
        bleDevices.clearDevices();
        scannedDeviceAdapter = new ListAdapter(this, bleDevices.getNames());
        //ble_list.setAdapter(scannedAddressAdapter);
        ble_list.setAdapter(scannedDeviceAdapter);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = User7SelectActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getName()!=null){
                                ArrayList bleAddress = bleDevices.getAddress();
                                if(!bleAddress.contains(device)){
                                    Log.d("t1", device+"   " + device.getAddress()+"   " + device.getName());
                                    bleDevices.addDevice(device.getName(), device);
                                    scannedDeviceAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            };

}
