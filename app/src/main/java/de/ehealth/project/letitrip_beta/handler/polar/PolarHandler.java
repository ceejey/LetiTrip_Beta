package de.ehealth.project.letitrip_beta.handler.polar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PolarHandler {

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();

    private final String HEART_RATE_UUID = "00002a37-0000-1000-8000-00805f9b34fb";

    private boolean mDeviceSearch = false;
    private boolean mDeviceFound = false;
    private boolean mConnectionStateChanging = false;
    private boolean mConnected = false;
    private boolean mReceiveHeartRate = false;

    public boolean isDeviceSearch(){
        return mDeviceSearch;
    }

    public boolean isDeviceFound(){
        return mDeviceFound;
    }

    public boolean isConnectionStateChanging(){
        return mConnectionStateChanging;
    }

    public boolean isDeviceConnected(){
        return mConnected;
    }

    public boolean isReceiveHeartRate(){
        return mReceiveHeartRate;
    }

    private Activity mActivity; //Which Activity shall get informed?
    private TextView mTxtHeartRate; //Which Textview shall show the heartrate?
    private TextView mTxtStatus; //TODO

    public PolarHandler(Activity activity, TextView txtHeartRate, TextView txtStatus){
        mActivity = activity;
        mTxtHeartRate = txtHeartRate;
        mTxtStatus = txtStatus;
    }

    public PolarHandler(Activity activity, TextView txtHeartRate){
        mActivity = activity;
        mTxtHeartRate = txtHeartRate;
    }

    public PolarHandler(Activity activity){
        mActivity = activity;
    }

    public boolean isBluetoothEnabled(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.d("Bluetooth Adapter", "Bluetooth is disabled");
            Toast.makeText(mActivity, "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            Log.d("Bluetooth Adapter", "Bluetooth is enabled");
            Toast.makeText(mActivity, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void searchPolarDevice(){
        if(isBluetoothEnabled()) {
            Handler handler = new Handler();
            final long SCAN_PERIOD = 10000;

            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);

            mActivity.registerReceiver(mReceiver, filter);

            //Cancels discovery after 10 seconds.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mDeviceSearch) {
                        mBluetoothAdapter.cancelDiscovery();
                        mDeviceSearch = false;
                        Log.d("Bluetooth Adapter", "Canceled Discovery");
                        Toast.makeText(mActivity, "Canceled Discovery", Toast.LENGTH_SHORT).show();
                    }
                }
            }, SCAN_PERIOD);
            mDeviceSearch = true;
            mBluetoothAdapter.startDiscovery();
            Log.d("Bluetooth Adapter", "Started Discovery");
            Toast.makeText(mActivity, "Started Discovery", Toast.LENGTH_SHORT).show();
        }
    }

    public void connectToPolarDevice(BluetoothDevice device){
        mBluetoothAdapter.cancelDiscovery();
        mDeviceSearch = false;
        if(!mDeviceSearch && mDeviceFound && !mConnected && !mConnectionStateChanging) {
            mConnectionStateChanging = true;
            Toast.makeText(mActivity, "Trying to connect..", Toast.LENGTH_SHORT).show();
            mBluetoothGatt = device.connectGatt(mActivity, false, mGattCallback);
        }
        else
            Toast.makeText(mActivity, "Connection failed", Toast.LENGTH_LONG).show();
    }

    public void disconnectFromPolarDevice(){
        if(mConnected && !mConnectionStateChanging) {
            mConnectionStateChanging = true;
            Toast.makeText(mActivity, "Trying to disconnect", Toast.LENGTH_SHORT).show();
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        else
            Toast.makeText(mActivity, "Device not connected yet", Toast.LENGTH_SHORT).show();
    }

    public void receiveHeartRate(){
        if(mConnected && !mConnectionStateChanging && !mReceiveHeartRate) {
            Toast.makeText(mActivity, "Receiving heart rate now", Toast.LENGTH_SHORT).show();
            mReceiveHeartRate = true;
            mBluetoothGatt.discoverServices();
        }
        else
            Toast.makeText(mActivity, "Already receiving heart rate", Toast.LENGTH_SHORT).show();
    }

    public void stopReceivingHeartRate(){
        if(mConnected && !mConnectionStateChanging && mReceiveHeartRate){
            Toast.makeText(mActivity, "Stop receiving heart rate", Toast.LENGTH_SHORT).show();
            mReceiveHeartRate = false;
            mBluetoothGatt.discoverServices();
        }
        else
            Toast.makeText(mActivity, "Wasn't receiving heart rate", Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                mDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getName().contains("Polar")) {
                    Log.d("Bluetooth Device", "Found: " + mDevice.getName());
                    Toast.makeText(mActivity, "Found: " + mDevice.getName(), Toast.LENGTH_SHORT).show();
                    mDeviceFound = true;
                    //Log.d("Bluetooth Adapter", "Canceled Discovery");
                    //mBluetoothAdapter.cancelDiscovery();
                    BluetoothDevice device = mDevice; //Not tested whether the list entry gets changed when the same mDevice gets changed everytime a new device gets found.
                    mDeviceList.add(device);
                    //mDeviceSearch = false;
                }
            }
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("Statussss", "" + status);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.d("BluetoothGatt", "Connected to GATT server.");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mTxtStatus != null)
                            mTxtStatus.setText("Connected");
                        Toast.makeText(mActivity, "Connected to device", Toast.LENGTH_SHORT).show();
                    }
                });

                mConnected = true;
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothGatt", "Disconnected from GATT server.");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mTxtStatus != null)
                            mTxtStatus.setText("Disconnected");
                        Toast.makeText(mActivity, "Disconnected from device", Toast.LENGTH_SHORT).show();
                    }
                });

                mConnected = false;
            }
            mConnectionStateChanging = false;
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            List<BluetoothGattService> services = mBluetoothGatt.getServices();
            for (BluetoothGattService service : services) {
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                Log.d("Services", service.toString());
                for(BluetoothGattCharacteristic characteristic : characteristics){
                    Log.d("Characteristic", "" + characteristic.getUuid());

                    if(mReceiveHeartRate) {
                        if (characteristic.getUuid().toString().equals(HEART_RATE_UUID)) {
                            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                        }
                    }
                    else if(!mReceiveHeartRate){
                        if (characteristic.getUuid().toString().equals(HEART_RATE_UUID)) {
                            mBluetoothGatt.setCharacteristicNotification(characteristic, false);
                        }
                    }
                }
            }
        }

        @Override
        // Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("Characteristic read", "" + characteristic.getUuid());
            if(characteristic.getUuid().toString().contains("2a37")){
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) == 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    final int heartRate = characteristic.getIntValue(format, 1);
                    Log.d("Receive", String.format("Received heart rate: %d", heartRate));
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mTxtHeartRate != null)
                                mTxtHeartRate.setText(String.valueOf(heartRate));
                        }
                    });

                }else Log.d("Receive", "Unsupported format.");
            }
        }
    };

    public List<BluetoothDevice> getDeviceList() {
        return mDeviceList;
    }

}