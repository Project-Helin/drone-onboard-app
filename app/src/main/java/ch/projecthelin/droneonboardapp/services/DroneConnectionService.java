package ch.projecthelin.droneonboardapp.services;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.RouteDto;
import ch.projecthelin.droneonboardapp.listeners.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.listeners.MissionListener;
import ch.projecthelin.droneonboardapp.mappers.DroneStateMapper;
import ch.projecthelin.droneonboardapp.mappers.RouteMissionMapper;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.drone.DroneStateApi;
import com.o3dr.android.client.apis.drone.GuidedApi;
import com.o3dr.android.client.apis.mission.MissionApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeEventExtra;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.mission.Mission;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DroneConnectionService implements DroneListener, TowerListener {

    private static final String LOCAL_IP = "192.168.58.1";
    private static final int BAUD_RATE_FOR_USB = 115200;
    private static final int TCP_PORT = 5770;
    private static final int UDP_PORT = 14551;
    private static final int TAKEOFF_ALTITUDE = 10;
    private static final double ALTITUDE_INACCURACY_RATIO = 0.95;

    private final Handler handler = new Handler();
    private final Drone drone;
    private RouteMissionMapper missionMapper;
    private final ControlTower controlTower;
    private List<DroneConnectionListener> connectionListeners = new ArrayList<>();

    private DroneStateMapper droneStateMapper;
    private DroneState droneState = new DroneState();
    private GpsState gpsState = new GpsState();

    private BatteryState batteryState = new BatteryState();
    private int connectionType;
    private boolean startMission;
    private boolean endMissionWhenLanded;
    private MissionListener missionListener;

    private String droneName;
    private boolean isActive;
    private int payload;

    private int servoChannel;
    private int servoOpenPWM;
    private int servoClosedPWM;

    @Inject
    public DroneConnectionService(ControlTower controlTower, Drone drone, RouteMissionMapper missionMapper, DroneStateMapper droneStateMapper) {
        this.controlTower = controlTower;
        this.drone = drone;
        this.missionMapper = missionMapper;
        this.controlTower.connect(this);
        this.droneStateMapper = droneStateMapper;
    }


    public void connect() {
        Bundle extraParams = new Bundle();

        if (connectionType == ConnectionType.TYPE_USB) {
            extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, BAUD_RATE_FOR_USB);
        } else if (connectionType == ConnectionType.TYPE_TCP) {
            extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, LOCAL_IP);
            extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, TCP_PORT);
        } else if (connectionType == ConnectionType.TYPE_UDP) {
            extraParams.putString(ConnectionType.EXTRA_UDP_SERVER_PORT, LOCAL_IP);
            extraParams.putInt(ConnectionType.EXTRA_UDP_SERVER_PORT, UDP_PORT);
        }

        ConnectionParameter connectionParams = new ConnectionParameter(connectionType, extraParams, null);

        drone.connect(connectionParams);
    }

    public void disconnect() {
        drone.disconnect();
    }

    public void addConnectionListener(DroneConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }

    public void removeConnectionListener(DroneConnectionListener connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    public void setMissionListener(MissionListener missionListener) {
        this.missionListener = missionListener;
    }

    public void removeMissionListener() {
        this.missionListener = null;
    }

    private void notifyDroneStateListeners() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDroneStateChange(droneState);
        }
    }

    private void notifyGPSStateListeners() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onGpsStateChange(gpsState);
        }
    }

    private void notifyBatteryStateListeners() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onBatteryStateChange(batteryState);
        }
    }

    public void sendRouteToAutopilot(RouteDto route) {

        Mission mission = missionMapper.convertToMission(route, servoChannel, servoOpenPWM);
        MissionApi.setMission(drone, mission, true);
    }

    public void startMission() {
        startMission = true;
        DroneStateApi.setVehicleMode(drone, VehicleMode.COPTER_STABILIZE);
        DroneStateApi.arm(drone, true);
    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {

            case AttributeEvent.STATE_CONNECTED:
                handleDroneConnected();
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                handleDroneDisconnected();
                break;

            case AttributeEvent.STATE_UPDATED:
                break;

            case AttributeEvent.STATE_ARMING:
                if (startMission) {
                    DroneStateApi.setVehicleMode(drone, VehicleMode.COPTER_GUIDED);
                    GuidedApi.takeoff(drone, TAKEOFF_ALTITUDE);
                }
                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                break;

            case AttributeEvent.HOME_UPDATED:
                break;

            case AttributeEvent.TYPE_UPDATED:
            case AttributeEvent.ATTITUDE_UPDATED:
            case AttributeEvent.ALTITUDE_UPDATED:
                Altitude altitude = drone.getAttribute(AttributeType.ALTITUDE);

                if (startMission) {
                    startAutoPilotWhenTakeOffFinished(altitude);
                } else if (endMissionWhenLanded) {
                    if (altitude != null && altitude.getAltitude() < 1) {
                        missionListener.onMissionFinished();
                        endMissionWhenLanded = false;
                    }
                }
                break;
            case AttributeEvent.SPEED_UPDATED:
                break;
            case AttributeEvent.BATTERY_UPDATED:
                handleBatteryStateChange();
                break;

            case AttributeEvent.GPS_POSITION:
            case AttributeEvent.GPS_FIX:
            case AttributeEvent.GPS_COUNT:
            case AttributeEvent.WARNING_NO_GPS:
                handleGpsStateChange();
                break;
            case AttributeEvent.AUTOPILOT_MESSAGE:
                Log.i("DRONE_EVENT", extras.getString(AttributeEventExtra.EXTRA_AUTOPILOT_MESSAGE));

            default:

                break;
        }
    }

    private void startAutoPilotWhenTakeOffFinished(Altitude altitude) {

        double minAltitudeToStartMission = TAKEOFF_ALTITUDE * ALTITUDE_INACCURACY_RATIO;
        if (altitude != null && altitude.getTargetAltitude() > minAltitudeToStartMission && minAltitudeToStartMission < altitude.getAltitude()) {
            DroneStateApi.setVehicleMode(drone, VehicleMode.COPTER_AUTO);
            startMission = false;
            endMissionWhenLanded = true;
        }
    }

    private void handleDroneConnected() {
        droneState = droneStateMapper.getDroneState(drone);
        droneState.setIsConnected(true);
        this.notifyDroneStateListeners();
    }

    private void handleDroneDisconnected() {
        clearDroneData();

        notifyDroneStateListeners();
        notifyGPSStateListeners();
        notifyBatteryStateListeners();
    }

    private void handleBatteryStateChange() {
        Battery droneBattery = drone.getAttribute(AttributeType.BATTERY);
        batteryState = droneStateMapper.getBatteryState(droneBattery);
        notifyBatteryStateListeners();
    }

    private void handleGpsStateChange() {
        Gps gps = drone.getAttribute(AttributeType.GPS);
        if (gps != null) {
            gpsState = droneStateMapper.getGPSState(gps);
            notifyGPSStateListeners();
        }

        DroneState newDroneState = droneStateMapper.getDroneState(drone);
        newDroneState.setIsConnected(droneState.isConnected());
        droneState = newDroneState;
    }

    private void clearDroneData() {
        droneState = new DroneState();
        gpsState = new GpsState();
        batteryState = new BatteryState();
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {

    }

    @Override
    public void onTowerConnected() {
        this.controlTower.registerDrone(this.drone, this.handler);
        this.drone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {
        this.drone.unregisterDroneListener(this);
        this.controlTower.unregisterDrone(this.drone);
    }

    public BatteryState getBatteryState() {
        return batteryState;
    }


    public DroneState getDroneState() {
        return droneState;
    }

    public Drone getDrone() {
        return drone;
    }

    public GpsState getGpsState() {
        return gpsState;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {

    }

    public int getServoChannel() {
        return servoChannel;
    }

    public void setServoChannel(int servoChannel) {
        this.servoChannel = servoChannel;
    }

    public int getServoOpenPWM() {
        return servoOpenPWM;
    }

    public void setServoOpenPWM(int servoPWM) {
        this.servoOpenPWM = servoPWM;
    }

    public void setServoClosedPWM(int servoClosedPWM) {
        this.servoClosedPWM = servoClosedPWM;
    }

    public int getServoClosedPWM() {
        return servoClosedPWM;
    }

    public String getDroneName() {
        return droneName;
    }

    public void setDroneName(String droneName) {
        this.droneName = droneName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }


}

