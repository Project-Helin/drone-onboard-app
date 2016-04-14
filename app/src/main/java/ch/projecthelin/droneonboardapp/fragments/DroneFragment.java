package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;

import ch.helin.messages.dto.state.GpsState;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;

import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;

import javax.inject.Inject;

public class DroneFragment extends Fragment implements DroneConnectionListener {

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView txtGps;
    private TextView txtPosition;
    private TextView txtBattery;
    private TextView txtAltitude;
    private TextView txtSpeed;
    private TextView txtFirmware;

    private Button btnConnect;
    private Spinner connectionSelector;
    private TextView txtErrorLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        droneConnectionService.addConnectionListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drone, container, false);

        initializeViewFields(view);
        initializeBtnListeners();
        initializeConnectionModeSpinner(view);

        return view;
    }

    private void initializeBtnListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (droneConnectionService.getDroneState().isConnected()) {
                    droneConnectionService.disconnect();
                } else {
                    droneConnectionService.connect();
                }
            }
        });
    }

    private void initializeViewFields(View view) {
        txtGps = (TextView) view.findViewById(R.id.txtGPS);
        txtPosition = (TextView) view.findViewById(R.id.txtPosition);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtAltitude = (TextView) view.findViewById(R.id.txtAltitude);
        txtSpeed = (TextView) view.findViewById(R.id.txtSpeed);
        txtFirmware = (TextView) view.findViewById(R.id.txtFirmware);
        txtErrorLog = (TextView) view.findViewById(R.id.txtErrorLog);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
    }

    protected void initializeConnectionModeSpinner(View view) {
        String[] connectionModes = {"USB", "UDP", "TCP"};
        connectionSelector = (Spinner) view.findViewById(R.id.connectionSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, connectionModes);
        connectionSelector.setAdapter(adapter);

        connectionSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onConnectionSelected(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    public void onConnectionSelected(View view) {
        int connectionType = (int) connectionSelector.getSelectedItemPosition();
        droneConnectionService.setConnectionType(connectionType);
    }

    @Override
    public void onDroneStateChange(DroneState state) {
        try {
            txtSpeed.setText("Ground: " + (int) (state.getGroundSpeed()) + "m/s Vertical: " + (int) (state.getVerticalSpeed()) + "m/s");
            txtAltitude.setText((int) state.getAltitude() + " / " + (int) state.getTargetAltitude());
            txtFirmware.setText(state.getFirmware());

            if (state.isConnected()) {
                btnConnect.setText("Disconnect");
            } else {
                btnConnect.setText("Connect");
            }
        } catch (Exception e) {
            txtErrorLog.setText(txtErrorLog.getText() + "Problem in onDroneStateChange \n");
        }


    }



    @Override
    public void onGpsStateChange(GpsState state) {
        try {
            txtGps.setText(state.getFixType().getDescription() + " - Satellites: "
                    + state.getSatellitesCount());
            txtPosition.setText(state.getPosLat() + ", " + state.getPosLon());
        } catch (Exception e) {
            txtErrorLog.setText(txtErrorLog.getText() + "Problem in onGpsStateChange \n");
        }
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        try {
            txtBattery.setText(state.getRemain() + "% - " + state.getVoltage() + "V, " + state.getCurrent() + "A");
        } catch (Exception e) {
            txtErrorLog.setText(txtErrorLog.getText() + "Problem in onBatteryStateChange \n");
        }


    }
}
