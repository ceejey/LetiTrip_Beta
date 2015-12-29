package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.DevicesAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.DevicesRow;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;


public class Device extends Fragment {

    private FragmentChanger mListener;
    private DialogInterface.OnClickListener mDialogListener;
    private ListView mDeviceListView;
    private int mSelectedDevice = -1;
    private ListAdapter mCustomAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_device, container, false);
        mDeviceListView = (ListView) view.findViewById(R.id.listDevices);
        ImageView imgAddDevice = (ImageView) view.findViewById(R.id.imgAddDevice);
        imgAddDevice.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
        imgAddDevice.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        showDevicesDialog();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.getDrawable().setColorFilter(0xff5c6bc0, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        List<DevicesRow> itemList = new ArrayList<>();
        if (!FitbitUserProfile.getmActiveUser().getmEncodedId().equals("")) {
            itemList.add(new DevicesRow(1,"Fitbit","Benutzername: " + FitbitUserProfile.getmActiveUser().getmFullname()));
        }
        if (!Polar.getmPolarDevice().equals("")){
            itemList.add(new DevicesRow(2,"Polar",Polar.getmPolarDevice()));
        }
        //ON CLICK LISTENER !!
        mCustomAdapter = new DevicesAdapter(getActivity(), itemList);
        mDeviceListView.setAdapter(mCustomAdapter);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDevice = position;
                showDeviceDialog(position);
            }
        });
        return view;
    }

    public void showDeviceDialog(int id) {

        DialogFragment newFragment = DeviceSelectorDialog.newInstance(id);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getFragmentManager().beginTransaction(), "DialogTag");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                switch (resultCode){
                    case 0:

                        break;
                    case 1:
                        if(mSelectedDevice == 0){
                            if(!Polar.getmPolarDevice().equals("")){
                                Polar.setmPolarDevice("");
                                updateActivity(MainActivity.FragmentName.SETTINGS_DEVICE);
                            }
                            else {
                                //FitbitUserProfile.deleteUser(getActivity());
                                FitBitUserDataSQLite.getInstance(getActivity()).newTable();
                                updateActivity(MainActivity.FragmentName.SETTINGS_DEVICE);
                            }

                        }
                        if(mSelectedDevice == 1){
                            Polar.setmPolarDevice("");
                            updateActivity(MainActivity.FragmentName.SETTINGS_DEVICE);
                        }
                        break;
                    case 2:

                        break;
                    default:
                        break;
                }
                break;
        }
    }


    public void showDevicesDialog() {

        DialogFragment newFragment = DeviceCreateDialog.newInstance();
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getFragmentManager().beginTransaction(), "DialogTag");
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
}
