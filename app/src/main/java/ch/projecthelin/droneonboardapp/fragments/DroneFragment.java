package ch.projecthelin.droneonboardapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.DroneActiveState;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsState;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.listeners.DroneAttributeUpdateReceiver;
import ch.projecthelin.droneonboardapp.listeners.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.o3dr.android.client.apis.drone.ExperimentalApi;

import javax.inject.Inject;

public class DroneFragment extends Fragment implements DroneConnectionListener, DroneAttributeUpdateReceiver {

    @Inject
    DroneConnectionService droneConnectionService;

    @Inject
    MessagingConnectionService messagingConnectionService;

    private TextView txtGps;
    private TextView txtBattery;
    private TextView txtAltitude;

    private Button btnConnect;
    private Spinner connectionSelector;
    private EditText editChannel;
    private Button btnSaveServoValues;
    private EditText editOpenPWM;
    private EditText editClosedPWM;
    private Button btnSetServo;

    private Switch switchDroneActive;

    private boolean isServoOpen;

    private JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();

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

        initializeServoValues();
        updateConnectButtonText(droneConnectionService.getDroneState());

        return view;
    }

    private void initializeViewComponents(View view) {
        txtGps = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtAltitude = (TextView) view.findViewById(R.id.txtAltitude);
        btnSaveServoValues = (Button) view.findViewById(R.id.btnSaveServoValues);
        editChannel = (EditText) view.findViewById(R.id.editChannel);
        editOpenPWM = (EditText) view.findViewById(R.id.editOpenPWM);
        editClosedPWM = (EditText) view.findViewById(R.id.editClosedPWM);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
        btnSetServo = (Button) view.findViewById(R.id.btnSetServo);
        switchDroneActive = (Switch) view.findViewById(R.id.swtchActive);
    }

    private void initializeServoValues() {
        editChannel.setText(String.valueOf(droneConnectionService.getServoChannel()));
        editOpenPWM.setText(String.valueOf(droneConnectionService.getServoOpenPWM()));
        editClosedPWM.setText(String.valueOf(droneConnectionService.getServoClosedPWM()));
    }


    private void toggleServo() {
        int pwm;
        String buttonText;

        if (isServoOpen) {
            pwm = droneConnectionService.getServoClosedPWM();
            isServoOpen = false;
            buttonText = "Open Servo";
        } else {
            pwm = droneConnectionService.getServoOpenPWM();
            isServoOpen = true;
            buttonText = "Close Servo";
        }
        ExperimentalApi.setServo(droneConnectionService.getDrone(), droneConnectionService.getServoChannel(), pwm);
        btnSetServo.setText(buttonText);
    }

    private void initializeBtnListeners() {
        btnSetServo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServo();
            }
        });

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

                Toast.makeText(getContext(), "Servo-Values saved!", Toast.LENGTH_SHORT).show();
            }
        });

        switchDroneActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DroneActiveState droneActiveState = new DroneActiveState();
                droneActiveState.setActive(isChecked);

                DroneActiveStateMessage droneActiveStateMessage = new DroneActiveStateMessage();
                droneActiveStateMessage.setDroneActiveState(droneActiveState);

                messagingConnectionService.sendMessage(jsonBasedMessageConverter.parseMessageToString(droneActiveStateMessage));
            }
        });
    }


    private void setServoValuesToDroneConnectionService(int channel, int openPWM, int closedPWM) {
        droneConnectionService.setServoChannel(channel);
        droneConnectionService.setServoOpenPWM(openPWM);
        droneConnectionService.setServoClosedPWM(closedPWM);
    }

    private void saveServoValuesToSharedPreferences(int channel, int openPWM, int closedPWM) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.CHANNEL_KEY, channel);
        editor.putInt(MainActivity.OPEN_PWM_KEY, openPWM);
        editor.putInt(MainActivity.CLOSED_PWM_KEY, closedPWM);
        editor.apply();
    }

    private void updateConnectButtonText(DroneState state) {
        if (state.isConnected()) {
            btnConnect.setText("Disconnect");
        } else {
            btnConnect.setText("Connect");
        }
    }

    protected void initializeConnectionModeSpinner(View view) {
        String[] connectionModes = {"USB", "UDP", "TCP"};
        connectionSelector = (Spinner) view.findViewById(R.id.connectionSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, connectionModes);
        connectionSelector.setAdapter(adapter);

        connectionSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onConnectionTypeSelected(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void onConnectionTypeSelected(View view) {
        int connectionType = connectionSelector.getSelectedItemPosition();
        droneConnectionService.setConnectionType(connectionType);
    }

    @Override
    public void onDroneStateChange(DroneState state) {
        if (state != null && state.getAltitude() > 0.1) {
            txtAltitude.setText((int) state.getAltitude() + " / " + (int) state.getTargetAltitude());
            updateConnectButtonText(state);
        } else {
            txtAltitude.setText("");
        }
    }

    @Override
    public void onGpsStateChange(GpsState state) {
        if (state.getFixType() != null) {
            txtGps.setText(state.getFixType().getDescription() + " - Satellites: "
                    + state.getSatellitesCount());
        } else {
            txtGps.setText("");
        }
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        if (state != null && state.getVoltage() > 0.1){
            txtBattery.setText(state.getRemain() + "% - " + state.getVoltage() + "V, " + state.getCurrent() + "A");
        }else{
            txtBattery.setText("");
        }


    }

    @Override
    public void onDroneAttributeUpdate(DroneDtoMessage droneDtoMessage) {

    }
}
