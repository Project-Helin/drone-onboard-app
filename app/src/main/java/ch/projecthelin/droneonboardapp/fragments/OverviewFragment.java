package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.helin.messages.dto.message.DroneDto;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsQuality;
import ch.helin.messages.dto.state.GpsState;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.listeners.DroneAttributeUpdateReceiver;
import ch.projecthelin.droneonboardapp.listeners.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

import javax.inject.Inject;


public class OverviewFragment extends Fragment implements DroneConnectionListener, MessagingConnectionListener, DroneAttributeUpdateReceiver {

    @Inject
    DroneConnectionService droneConnectionService;

    @Inject
    MessagingConnectionService messagingConnectionService;

    private static final int BATTERY_LOW = 10;

    private static final String PAYLOAD_STRING = "g Payload";

    private TextView txtConnection;
    private TextView txtGPS;
    private TextView txtBattery;
    private TextView txtServerConnectionState;
    private TextView txtActiveState;
    private TextView txtName;
    private TextView txtPayload;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        droneConnectionService.addConnectionListener(this);
        messagingConnectionService.addConnectionListener(this);
        messagingConnectionService.addDroneAttributeUpdateReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        initializeViewComponents(view);

        updateStatusColorsAndTexts();
        initializeDroneStateValues();

        return view;
    }

    private void initializeViewComponents(View view) {
        txtConnection = (TextView) view.findViewById(R.id.txtConnection);
        txtGPS = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtServerConnectionState = (TextView) view.findViewById(R.id.server_connection_state);
        txtActiveState = (TextView) view.findViewById(R.id.txtActive);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtPayload = (TextView) view.findViewById(R.id.txtPayload);
    }

    private void initializeDroneStateValues(){
        String droneName = droneConnectionService.getDroneName();
        int dronePayload = droneConnectionService.getPayload();
        Boolean isActive = droneConnectionService.isActive();

        if (isActive){
            txtActiveState.setText("Active");
            txtActiveState.setBackgroundResource(R.color.green);
        } else{
            txtActiveState.setText("Inactive");
            txtActiveState.setBackgroundResource(R.color.red);
        }

        txtName.setText(droneName);
        txtPayload.setText(String.valueOf(dronePayload) + PAYLOAD_STRING);
    }

    private void updateStatusColorsAndTexts() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                DroneState droneState = droneConnectionService.getDroneState();
                GpsState gpsState = droneConnectionService.getGpsState();
                BatteryState batteryState = droneConnectionService.getBatteryState();
                MessagingConnectionService.ConnectionState serverConnectionState = messagingConnectionService.getConnectionState();

                String droneName = droneConnectionService.getDroneName();
                int dronePayload = droneConnectionService.getPayload();
                Boolean isActive = droneConnectionService.isActive();

                if (droneState.isConnected()) {
                    txtConnection.setText("Connected");
                    txtConnection.setBackgroundResource(R.color.green);

                } else {
                    txtConnection.setText("Not Connected");
                    txtConnection.setBackgroundResource(R.color.red);
                }

                if (gpsState != null && gpsState.getFixType() != null) {
                    txtGPS.setText("GPS: " + gpsState.getFixType().getDescription());
                    if (gpsState.getFixType() != GpsQuality.NO_FIX) {
                        txtGPS.setBackgroundResource(R.color.green);
                    } else {
                        txtGPS.setBackgroundResource(R.color.red);
                    }
                }

                if (batteryState != null) {
                    txtBattery.setText("Battery: " + batteryState.getRemain() + "% - " + batteryState.getVoltage() + "V");
                    if (batteryState.getRemain() < BATTERY_LOW) {
                        txtBattery.setBackgroundResource(R.color.red);
                    } else {
                        txtBattery.setBackgroundResource(R.color.green);
                    }
                }

                if (serverConnectionState.equals(MessagingConnectionService.ConnectionState.CONNECTED)) {
                    txtServerConnectionState.setText("Connected");
                    txtServerConnectionState.setBackgroundResource(R.color.green);
                } else {
                    txtServerConnectionState.setText("Disconnected");
                    txtServerConnectionState.setBackgroundResource(R.color.red);
                }

                if (isActive){
                    txtActiveState.setText("Active");
                    txtActiveState.setBackgroundResource(R.color.green);
                } else{
                    txtActiveState.setText("Inactive");
                    txtActiveState.setBackgroundResource(R.color.red);
                }

                txtName.setText(droneName);
                txtPayload.setText(String.valueOf(dronePayload) + PAYLOAD_STRING);
            }
        });

    }

    @Override
    public void onDroneStateChange(DroneState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onGpsStateChange(GpsState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onConnectionStateChanged(final MessagingConnectionService.ConnectionState state) {
        updateStatusColorsAndTexts();
    }

    @Override
    public void onDroneAttributeUpdate(DroneDtoMessage droneDtoMessage) {
        DroneDto droneDto = droneDtoMessage.getDroneDto();

        droneConnectionService.setPayload(droneDto.getPayload());
        droneConnectionService.setDroneName(droneDto.getName());
        droneConnectionService.setIsActive(droneDto.isActive());

        updateStatusColorsAndTexts();
    }
}
