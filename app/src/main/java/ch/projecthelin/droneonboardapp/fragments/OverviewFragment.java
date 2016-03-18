package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.MessagingListener;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

import javax.inject.Inject;


public class OverviewFragment extends Fragment implements DroneConnectionListener, MessagingListener {

    private TextView txtConnection;
    private TextView txtGPS;
    private TextView txtBattery;
    private TextView txtServerConnectionState;

    private static final int BATTERY_LOW = 10;

    @Inject
    DroneConnectionService droneConnectionService;
    @Inject
    MessagingConnectionService messagingConnectionService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        droneConnectionService.addConnectionListener(this);
        messagingConnectionService.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        txtConnection = (TextView) view.findViewById(R.id.txtConnection);
        txtGPS = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtServerConnectionState = (TextView) view.findViewById(R.id.server_connection_state);

        updateStatusColorsAndTexts();

        return view;
    }

    private void updateStatusColorsAndTexts() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                DroneState droneState = droneConnectionService.getDroneState();
                GPSState gpsState = droneConnectionService.getGpsState();
                BatteryState batteryState = droneConnectionService.getBatteryState();
                MessagingConnectionService.ConnectionState serverConnectionState = messagingConnectionService.connectionState;

                if (droneState.isConnected()) {
                    txtConnection.setText("Connected");
                    txtConnection.setBackgroundResource(R.color.green);

                } else {
                    txtConnection.setText("Not Connected");
                    txtConnection.setBackgroundResource(R.color.red);
                }

                if(gpsState != null) {
                    txtGPS.setText("GPS: " + gpsState.getFixTypeLabel());
                    if (gpsState.isGPSGood()) {
                        txtGPS.setBackgroundResource(R.color.green);
                    } else {
                        txtGPS.setBackgroundResource(R.color.red);
                    }
                }

                if (batteryState != null) {
                    txtBattery.setText("Battery: " + batteryState.getRemain() + "%");
                    if (batteryState.getRemain() < BATTERY_LOW) {
                        txtBattery.setBackgroundResource(R.color.red);
                    } else {
                        txtBattery.setBackgroundResource(R.color.green);
                    }
                }

                if (serverConnectionState.equals(MessagingConnectionService.ConnectionState.CONNECTED)) {
                    txtServerConnectionState.setText(MessagingConnectionService.ConnectionState.CONNECTED.name());
                    txtServerConnectionState.setBackgroundResource(R.color.green);
                } else {
                    txtServerConnectionState.setText(MessagingConnectionService.ConnectionState.DISCONNECTED.name());
                    txtServerConnectionState.setBackgroundResource(R.color.red);
                }
            }
        });


    }


    @Override
    public void onDroneStateChange(DroneState state) {
       updateStatusColorsAndTexts();
    }

    @Override
    public void onGPSStateChange(GPSState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onMessageReceived(String message) {
    }

    @Override
    public void onConnectionStateChanged(final MessagingConnectionService.ConnectionState state) {
        updateStatusColorsAndTexts();
    }
}
