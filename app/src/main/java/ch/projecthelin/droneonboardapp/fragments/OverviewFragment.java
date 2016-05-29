package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.OrderProductDto;
import ch.helin.messages.dto.ProductDto;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;

import ch.helin.messages.dto.state.GpsQuality;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import ch.projecthelin.droneonboardapp.R;


import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.listeners.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.o3dr.android.client.apis.drone.ExperimentalApi;

import javax.inject.Inject;
import java.util.ArrayList;


public class OverviewFragment extends Fragment implements DroneConnectionListener, MessagingConnectionListener {

    private static final int BATTERY_LOW = 10;

    @Inject
    DroneConnectionService droneConnectionService;

    @Inject
    MessagingConnectionService messagingConnectionService;

    private TextView txtConnection;
    private TextView txtGPS;
    private TextView txtBattery;
    private TextView txtServerConnectionState;
    private boolean isServoOpen;
    private Button button;
    private Button setServoButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        droneConnectionService.addConnectionListener(this);
        messagingConnectionService.addConnectionListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        initializeViewComponents(view);
        initializeButtonListeners();

        updateStatusColorsAndTexts();

        return view;
    }

    private void initializeViewComponents(View view) {
        txtConnection = (TextView) view.findViewById(R.id.txtConnection);
        txtGPS = (TextView) view.findViewById(R.id.txtGPS);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtServerConnectionState = (TextView) view.findViewById(R.id.server_connection_state);
        button = (Button) view.findViewById(R.id.button);
        setServoButton = (Button) view.findViewById(R.id.setServoButton);
    }

    private void initializeButtonListeners() {
        setServoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServo();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();

                FinalAssignMissionMessage assignMissionMessage = new FinalAssignMissionMessage();
                MissionDto mission = new MissionDto();
                ProductDto product = new ProductDto();
                product.setName("Coca Cola");

                OrderProductDto orderProduct = new OrderProductDto();
                orderProduct.setAmount(20);
                orderProduct.setProduct(product);

                mission.setOrderProduct(orderProduct);
                RouteDto routeDto = new RouteDto();

                ArrayList<Waypoint> wayPoints = new ArrayList<>();
                Waypoint wp1 = new Waypoint();
                wp1.setAction(Action.FLY);
                wp1.setPosition(new Position(47.223343, 8.818480, 20));
                wayPoints.add(wp1);

                Waypoint wp2 = new Waypoint();
                wp1.setAction(Action.FLY);
                wp2.setPosition(new Position(47.222614, 8.817452, 5));
                wayPoints.add(wp2);

                Waypoint wp6 = new Waypoint();
                wp6.setAction(Action.FLY);
                wp6.setPosition(new Position(47.223511, 8.81550, 100));
                wayPoints.add(wp6);

                Waypoint wp3 = new Waypoint();
                wp3.setAction(Action.DROP);
                wp3.setPosition(new Position(47.223510, 8.815553, 50));
                wayPoints.add(wp3);

                Waypoint wp4 = new Waypoint();
                wp1.setAction(Action.FLY);
                wp4.setPosition(new Position(47.224209, 8.818933, 15));
                wayPoints.add(wp4);

                routeDto.setWayPoints(wayPoints);

                mission.setRoute(routeDto);

                assignMissionMessage.setMission(mission);

                activity.onFinalAssignMissionMessageReceived(assignMissionMessage);
            }
        });
    }

    private void toggleServo() {
        int pwm;
        String buttonText;

        if (isServoOpen) {
            pwm = droneConnectionService.getServoClosedPWM();
            isServoOpen = false;
            buttonText = "Open Servo!";
        } else {
            pwm = droneConnectionService.getServoOpenPWM();
            isServoOpen = true;
            buttonText = "Close Servo!";
        }
        ExperimentalApi.setServo(droneConnectionService.getDrone(), droneConnectionService.getServoChannel(), pwm);
        setServoButton.setText(buttonText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        droneConnectionService.removeConnectionListener(this);
        messagingConnectionService.removeConnectionListener(this);

    }

    private void updateStatusColorsAndTexts() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                DroneState droneState = droneConnectionService.getDroneState();
                GpsState gpsState = droneConnectionService.getGpsState();
                BatteryState batteryState = droneConnectionService.getBatteryState();
                MessagingConnectionService.ConnectionState serverConnectionState = messagingConnectionService.getConnectionState();

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
}
