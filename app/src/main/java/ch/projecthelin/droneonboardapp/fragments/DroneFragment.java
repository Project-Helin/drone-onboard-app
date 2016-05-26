package ch.projecthelin.droneonboardapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;

import javax.inject.Inject;

public class DroneFragment extends Fragment implements DroneConnectionListener {

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView txtGps;
    private TextView txtBattery;
    private TextView txtAltitude;

    private Button btnConnect;
    private Spinner connectionSelector;
    private EditText editChannel;
    private Button btnSaveServoValues;
    private EditText editOpenPWM;
    private EditText editClosedPWM;

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

        initializeViewComponents(view);
        initializeBtnListeners();
        initializeConnectionModeSpinner(view);

        setServoValues();

        return view;
    }

    private void setServoValues() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String channel = String.valueOf(sharedPreferences.getInt(MainActivity.CHANNEL_KEY, 6));
        editChannel.setText(channel);

        String openPWD = String.valueOf(sharedPreferences.getInt(MainActivity.OPEN_PWM_KEY,1800));
        String closedPWD = String.valueOf(sharedPreferences.getInt(MainActivity.CLOSED_PWM_KEY,1400));
        editOpenPWM.setText(openPWD);
        editClosedPWM.setText(closedPWD);
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

        btnSaveServoValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int channel = Integer.valueOf(editChannel.getText().toString());
                int openPWM = Integer.valueOf(editOpenPWM.getText().toString());
                int closedPWM = Integer.valueOf(editClosedPWM.getText().toString());

                saveServoValuesToSharedPreferences(channel, openPWM, closedPWM);
                setServoValuesToDroneConnectionService(channel, openPWM, closedPWM);
            }
        });
    }

    private void setServoValuesToDroneConnectionService(int channel, int openPWM, int closedPWM) {
        droneConnectionService.setServoChannel(channel);
        droneConnectionService.setServoOpenPWM(openPWM);
        droneConnectionService.setServoClosedPWM(closedPWM);
    }

    private void initializeViewComponents(View view) {
        txtGps = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtAltitude = (TextView) view.findViewById(R.id.txtAltitude);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
        btnSaveServoValues = (Button) view.findViewById(R.id.btnSaveServoValues);
        editChannel = (EditText) view.findViewById(R.id.editChannel);
        editOpenPWM = (EditText) view.findViewById(R.id.editOpenPWM);
        editClosedPWM = (EditText) view.findViewById(R.id.editClosedPWM);
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

    private void saveServoValuesToSharedPreferences(int channel, int openPWM, int closedPWM) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.CHANNEL_KEY, channel);
        editor.putInt(MainActivity.OPEN_PWM_KEY, openPWM);
        editor.putInt(MainActivity.CLOSED_PWM_KEY, closedPWM);
        editor.apply();
    }

    @Override
    public void onDroneStateChange(DroneState state) {
        try {
            txtAltitude.setText((int) state.getAltitude() + " / " + (int) state.getTargetAltitude());

            if (state.isConnected()) {
                btnConnect.setText("Disconnect");
            } else {
                btnConnect.setText("Connect");
            }
        } catch (Exception e) {
            Log.d("Error", "Problem in onDroneStateChange");
        }
    }


    @Override
    public void onGpsStateChange(GpsState state) {
        try {
            txtGps.setText(state.getFixType().getDescription() + " - Satellites: "
                    + state.getSatellitesCount());
        } catch (Exception e) {
           Log.d("Error", "Problem in onGpsStateChange");
        }
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        try {
            txtBattery.setText(state.getRemain() + "% - " + state.getVoltage() + "V, " + state.getCurrent() + "A");
        } catch (Exception e) {
            Log.d("Error", "Problem in onBatteryStateChange");
        }


    }
}
