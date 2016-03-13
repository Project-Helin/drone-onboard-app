package ch.projecthelin.droneonboardapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;

import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.dto.dronestate.AltitudeState;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;

public class DroneFragment extends Fragment implements DroneConnectionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // here comment
    public static final String TCP_SERVER_IP = "192.168.56.1";
    public static final int BAUD_RATE_FOR_USB = 115200;
    public static final int TCP_SERVER_PORT = 5760;

    private DroneConnectionService droneConnectionService;

    private ConnectionParameter connectionParams;

    private TextView txtGps;
    private TextView txtPosition;
    private TextView txtBattery;
    private TextView txtAltitude;
    private TextView txtSpeed;
    private TextView txtFirmware;

    private Button btnConnect;
    private DroneState droneState;
    private boolean isConnected = false;

    public DroneFragment() {
        // Required empty public constructor
        droneConnectionService = DroneConnectionService.getInstance(this.getContext());
        droneConnectionService.addConnectionListener(this);


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DroneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DroneFragment newInstance(String param1, String param2) {
        DroneFragment fragment = new DroneFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drone, container, false);

        txtGps = (TextView) view.findViewById(R.id.txtGPS);
        txtPosition = (TextView) view.findViewById(R.id.txtPosition);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtAltitude = (TextView) view.findViewById(R.id.txtAltitude);
        txtSpeed = (TextView) view.findViewById(R.id.txtSpeed);
        txtFirmware = (TextView) view.findViewById(R.id.txtFirmware);

        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnConnectToDrone:
                        if (isConnected == false) {
                            btnConnect.setText("Connecting ...");
                            btnConnect.setEnabled(false);
                            droneConnectionService.connect();
                        } else {
                            droneConnectionService.disconnect();
                            btnConnect.setText("Connect");
                        }
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


  /*//  @Override
    public void onConnectionStateChange(DroneState state) {
      *//*  this.droneState = state;
        if(state instanceof ConnectionState){
            ConnectionState connectionState = (ConnectionState) state;
            this.isConnected = connectionState.isConnected();
            if(this.isConnected == true){
                btnConnect.setText("Disconnect");
                btnConnect.setEnabled(true);
            } if(this.isConnected == false) {
                btnConnect.setText("Connect");
                btnConnect.setEnabled(true);
            }

        }
        if(state instanceof AltitudeState){
            AltitudeState altitudeState = (AltitudeState) state;
            txtAltitude.setText((int) altitudeState.getAltitude() + " / " + (int) altitudeState.getTargetAltitude());

        }
        if(state instanceof SpeedState){
            SpeedState speedState = (SpeedState) state;
            txtSpeed.setText("Ground: " + (int) (speedState.getGroundSpeed()) + "m/s Vertical: " + (int) (speedState.getVerticalSpeed()) + "m/s");


        }
        if(state instanceof BatteryState){
            BatteryState batteryState = (BatteryState) state;
            txtBattery.setText(batteryState.getRemain() + "% - " + batteryState.getVoltage() + "V, " + batteryState.getCurrent() + "A");

        }
        if(state instanceof GPSState){
            GPSState gpsState =  (GPSState) state;
            txtGps.setText(gpsState.getFixType() + " - Sattelites: "
                    + gpsState.getSattelitesCount());
            txtPosition.setText(gpsState.getLatLong());*//*
        }
    }*//*        Log.d("DroneFragment", state.toString());
        if(state.getIsConnected() == true){
            b.setText("disconnect");
        } if(state.getIsConnected() == false){
            b.setText("Connect");
        }
        if(state.getGPSState() != null){

        }

        if(state.getBatteryState() != null){
        }
    }*/

    @Override
    public void onDroneStateChange(DroneState state) {
        txtSpeed.setText("Ground: " + (int) (state.getGroundSpeed()) + "m/s Vertical: " + (int) (state.getVerticalSpeed()) + "m/s");
        txtAltitude.setText((int) state.getAltitude() + " / " + (int) state.getTargetAltitude());
        txtFirmware.setText(state.getFirmware());
    }

    @Override
    public void onGPSStateChange(GPSState state) {
        txtGps.setText(state.getFixType() + " - Sattelites: "
                + state.getSattelitesCount());
        txtPosition.setText(state.getLatLong());
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        txtBattery.setText(state.getRemain() + "% - " + state.getVoltage() + "V, " + state.getCurrent() + "A");

    }
}
