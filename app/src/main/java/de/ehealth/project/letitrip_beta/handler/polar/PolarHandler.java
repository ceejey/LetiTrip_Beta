package de.ehealth.project.letitrip_beta.handler.polar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class implements the bluetooth connection to the polar H6 device.
 */
public class PolarHandler {

    private PolarCallback callback;
    private static PolarHandler mInstance;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();

    private final String HEART_RATE_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    private final String HEART_RATE_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private boolean mDeviceSearch = false;
    private boolean mDeviceFound = false;
    private boolean mConnectionStateChanging = false;
    private boolean mConnected = false;
    private boolean mReceiveHeartRate = false;

    public static PolarHandler getInstance(){
        return mInstance;
    }

    public static void setInstance(PolarHandler instance){
        mInstance = instance;
    }

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
    private Context mContext; //if its started via the service
    private TextView mTxtHeartRate; //Which Textview shall show the heartrate?
    public static int mHeartRate = 0;
    private TextView mTxtStatus;

    public PolarHandler(Activity activity, TextView txtHeartRate, TextView txtStatus){
        mActivity = activity;
        mContext = mActivity;
        mTxtHeartRate = txtHeartRate;
        mTxtStatus = txtStatus;
    }

    public PolarHandler(Activity activity, TextView txtHeartRate){
        mActivity = activity;
        mContext = mActivity;
        mTxtHeartRate = txtHeartRate;
    }

    public PolarHandler(Activity activity){
        mActivity = activity;
        mContext = mActivity;
    }

    public PolarHandler(Context context, PolarCallback callback){
        mContext = context;
        this.callback = callback;
    }

    public PolarHandler(Context context){
        mContext = context;
    }

    public boolean isBluetoothEnabled(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        else{
            return true;
        }
    }

    public void searchPolarDevice(){
        if(isBluetoothEnabled()) {
            Handler handler = new Handler();
            final long SCAN_PERIOD = 8000;

            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);

            mContext.registerReceiver(mReceiver, filter);

            //Cancels discovery after 10 seconds.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mDeviceSearch) {
                        mBluetoothAdapter.cancelDiscovery();
                        mDeviceSearch = false;
                        if(callback != null){
                            callback.polarDiscoveryFinished();
                        }
                    }
                }
            }, SCAN_PERIOD);
            mDeviceSearch = true;
            mBluetoothAdapter.startDiscovery();
        }
    }

    public void stopSearchingPolarDevice(){
        if (mDeviceSearch) {
            mBluetoothAdapter.cancelDiscovery();
            mDeviceSearch = false;
            if(callback != null){
                callback.polarDiscoveryFinished();
            }
        }
    }

    public void connectToPolarDevice(BluetoothDevice device){
        mBluetoothAdapter.cancelDiscovery();
        mDeviceSearch = false;
        if(!mDeviceSearch && mDeviceFound && !mConnected && !mConnectionStateChanging) {
            mConnectionStateChanging = true;
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        }
    }

    public void disconnectFromPolarDevice(){
        if(mConnected && !mConnectionStateChanging) {
            mConnectionStateChanging = true;
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public void receiveHeartRate(){
        if(mConnected && !mConnectionStateChanging && !mReceiveHeartRate) {
            mReceiveHeartRate = true;
            mBluetoothGatt.discoverServices();
        }
    }

    public void stopReceivingHeartRate(){
        if(mConnected && !mConnectionStateChanging && mReceiveHeartRate){
            mReceiveHeartRate = false;
            mBluetoothGatt.discoverServices();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getName().contains("Polar")) {
                    mDeviceFound = true;

                    BluetoothDevice device = mDevice; //Not tested whether the list entry gets changed when the same mDevice gets changed everytime a new device gets found.
                    mDeviceList.add(device);
                }
            }
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                if (mActivity != null){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mTxtStatus != null)
                                mTxtStatus.setText("Connected");
                        }
                    });
                }

                mConnected = true;
                mConnectionStateChanging = false;
                if(callback != null){

                    callback.polarDeviceConnected();
                }
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mActivity != null){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mTxtStatus != null)
                                mTxtStatus.setText("Disconnected");
                        }
                    });
                }

                mConnected = false;
                mConnectionStateChanging = false;
            }
        }

        /**
         * Gets called at the very beginning of the connection, to receive the heartrate
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            List<BluetoothGattService> services = mBluetoothGatt.getServices();
            for (BluetoothGattService service : services) {

                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for(BluetoothGattCharacteristic characteristic : characteristics){

                    if(mReceiveHeartRate) {

                        if (characteristic.getUuid().toString().equals(HEART_RATE_UUID)) {
                            boolean result = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                    UUID.fromString(HEART_RATE_DESCRIPTOR_UUID));
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(descriptor);
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

        /**
         * This method receives the heartrate.
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(characteristic.getUuid().toString().contains("2a37")){
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) == 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    final int heartRate = characteristic.getIntValue(format, 1);
                    if (mActivity != null){
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mTxtHeartRate != null)
                                    mTxtHeartRate.setText(String.valueOf(heartRate));
                            }
                        });
                    }
                    mHeartRate = heartRate;

                }
            }
        }
    };

    public List<BluetoothDevice> getDeviceList() {
        return mDeviceList;
    }

}