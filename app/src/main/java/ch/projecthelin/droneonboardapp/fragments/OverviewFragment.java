package ch.projecthelin.droneonboardapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.activities.MissionActivity;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;

import javax.inject.Inject;


public class OverviewFragment extends Fragment implements DroneConnectionListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView txtConnection;
    private TextView txtGPS;
    private TextView txtBattery;

    private static final int BATTERY_LOW = 10; // If battery bellow 10%

    @Inject
    DroneConnectionService droneConnectionService;

    public OverviewFragment() {
        droneConnectionService.addConnectionListener(this);
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        txtConnection = (TextView) view.findViewById(R.id.txtConnection);
        txtGPS = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);


        return view;
    }

    @Override
    public void onDroneStateChange(DroneState state) {
        if(state.isConnected() == true){
            txtConnection.setBackgroundResource(R.color.green);

        } else{
            txtConnection.setBackgroundResource(R.color.red);
        }
    }

    @Override
    public void onGPSStateChange(GPSState state) {
        txtGPS.setText("GPS: " + state.getFixType());

        if(state.isGPSGood()){
            txtGPS.setBackgroundResource(R.color.green);
        } else{
            txtGPS.setBackgroundResource(R.color.red);
        }
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        txtBattery.setText("Battery: " + state.getRemain() + "%");
        if(state.getRemain() < BATTERY_LOW){
            txtBattery.setBackgroundResource(R.color.red);
        } else{
            txtBattery.setBackgroundResource(R.color.green);
        }
    }
}
