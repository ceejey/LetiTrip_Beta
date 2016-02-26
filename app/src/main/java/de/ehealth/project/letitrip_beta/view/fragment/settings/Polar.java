package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.DevicesAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.DevicesRow;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 27.12.2015.
 */


public class Polar extends Fragment {

    private FragmentChanger mListener;
    private static PolarHandler mPolar = null;
    private List<BluetoothDevice> mDeviceList = null;
    private ListView mLvPolarDeviceList = null;
    private boolean mSearch = true;
    private  ListAdapter mCustomAdapter = null;
    private  List<DevicesRow> mItemList = null;
    private int mSelectedDevice = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_polar, container, false);
        mPolar = new PolarHandler(getActivity(), (TextView) view.findViewById(R.id.txtHeartRate2), (TextView) view.findViewById(R.id.txtStatus));
        Button btnSearch = (Button) view.findViewById(R.id.btnSearchPolarH6);
        mLvPolarDeviceList = (ListView) view.findViewById(R.id.listViewPolarDevices);

      //  Button btnToggleHeartRate = (Button) view.findViewById(R.id.btnToggleHeartRate);
        btnSearch.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        mPolar.searchPolarDevice();
                        new WaitforPolarDevices(getActivity()).execute();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                switch (resultCode){
                    case 0:
                        if(mDeviceList.size() != 0) {
                            UserSettings.getmActiveUser().setmPolarDeviceID(mDeviceList.get(mSelectedDevice).getName());
                        }
                        UserSettings.getmActiveUser().setmPolarDeviceID("Nur zum Listen Test");
                        updateActivity(MainActivity.FragmentName.SETTINGS_DEVICE);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void showDeviceDialog(int id) {

        DialogFragment newFragment = PolarSelectorDialog.newInstance(id);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getFragmentManager().beginTransaction(), "DialogTag");
    }

    class WaitforPolarDevices extends AsyncTask<Void, Void, Void> {


        private Activity mActivity = null;
        private ProgressDialog dialog = null;

        public WaitforPolarDevices(Activity act){
            mActivity = act;
            dialog = new ProgressDialog(mActivity);
        }
        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Void v) {
            if(dialog != null) dialog.dismiss();
        }

        protected Void doInBackground(Void... params) {
            // updates the list for 2s
            Handler handler = new Handler(Looper.getMainLooper());
            mItemList = null;
            mSearch = true;
            final long SCAN_PERIOD = 4000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSearch = false;
                    mCustomAdapter = new DevicesAdapter(getActivity(), mItemList);
                    mLvPolarDeviceList.setAdapter(mCustomAdapter);
                    mLvPolarDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mSelectedDevice = position;
                            showDeviceDialog(position);
                        }
                    });
                }
            }, SCAN_PERIOD);
            while(mSearch){
                mDeviceList = mPolar.getDeviceList();
                mItemList = new ArrayList<>();
                if (mDeviceList.size() != 0) {
                    for (int i = 0; i < mDeviceList.size(); i++) {
                        mItemList.add(new DevicesRow(i,"Polar", mDeviceList.get(i).getName()));
                    }
                }
                else{
                    mItemList.add(new DevicesRow(1, "Polar", "NICHT GEFUNDEN!"));
                }
            }

            return null;
        }

    }
}