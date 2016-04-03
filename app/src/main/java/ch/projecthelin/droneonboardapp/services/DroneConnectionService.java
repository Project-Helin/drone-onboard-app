package ch.projecthelin.droneonboardapp.services;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.mappers.DroneStateMapper;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.drone.DroneStateApi;
import com.o3dr.android.client.apis.drone.GuidedApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DroneConnectionService implements DroneListener, TowerListener {

    public static final String TCP_SERVER_IP = "192.168.57.1";
    public static final int BAUD_RATE_FOR_USB = 115200;
    public static final int TCP_SERVER_PORT = 5760;

    private ControlTower controlTower;
    private Drone drone;
    private final Handler handler = new Handler();

    private List<DroneConnectionListener> connectionListeners = new ArrayList<>();
    private DroneState droneState = new DroneState();
    private boolean takeoffWhenArmed;
    private GPSState gpsState;
    private BatteryState batteryState;
    private int connectionType;

    @Inject
    public DroneConnectionService(ControlTower controlTower, Drone drone) {
        this.controlTower = controlTower;
        this.drone = drone;
        this.controlTower.connect(this);
    }

    public void connect() {
        Bundle extraParams = new Bundle();

        if (connectionType == ConnectionType.TYPE_USB) {
            extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, BAUD_RATE_FOR_USB);
        } else if (connectionType == ConnectionType.TYPE_TCP) {
            extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, TCP_SERVER_IP);
            extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, TCP_SERVER_PORT);
        }

        ConnectionParameter connectionParams = new ConnectionParameter(connectionType, extraParams, null);

        drone.connect(connectionParams);
    }

    public void disconnect() {
        drone.disconnect();
    }

    public DroneState getDroneState() {
        return droneState;
    }

    public GPSState getGpsState() {
        return gpsState;
    }

    public void addConnectionListener(DroneConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }

    public void triggerDroneStateChange() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDroneStateChange(droneState);
        }
    }

    public void triggerGPSStateChange() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onGPSStateChange(gpsState);
        }
    }

    public void triggerBatteryStateChange() {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onBatteryStateChange(batteryState);
        }
    }


    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {

            case AttributeEvent.STATE_CONNECTED:
                Log.d(getClass().getCanonicalName(), "STATE_CONNECTED");
                droneState = DroneStateMapper.getDroneState(drone);
                droneState.setIsConnected(true);
                this.triggerDroneStateChange();
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                Log.d(getClass().getCanonicalName(), "STATE_DISCONNECTED");
                //this clears the State of the drone
                clearDroneData();
                this.triggerDroneStateChange();
                break;

            case AttributeEvent.STATE_UPDATED:
                Log.d(getClass().getCanonicalName(), "STATE_UPDATED");

                break;

            case AttributeEvent.STATE_ARMING:
                Log.d(getClass().getCanonicalName(), "STATE_ARMING");
                if (takeoffWhenArmed) {
                    GuidedApi.takeoff(drone, 10);
                    takeoffWhenArmed = false;
                }

                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                Log.d(getClass().getCanonicalName(), "STATE_VEHICLE_MODE");
                break;

            case AttributeEvent.HOME_UPDATED:
                Log.d(getClass().getCanonicalName(), "HOME_UPDATED");
                break;
            case AttributeEvent.TYPE_UPDATED:
            case AttributeEvent.ATTITUDE_UPDATED:
            case AttributeEvent.ALTITUDE_UPDATED:
            case AttributeEvent.SPEED_UPDATED:
                Log.d(getClass().getCanonicalName(), "SPEED_UPDATED / ALTITUDE_UPDATED");
                DroneState state = DroneStateMapper.getDroneState(drone);
                triggerDroneStateChange();

                break;

            case AttributeEvent.BATTERY_UPDATED:
                Log.d(getClass().getCanonicalName(), "BATTERY_UPDATED");
                Battery droneBattery = drone.getAttribute(AttributeType.BATTERY);
                batteryState = DroneStateMapper.getBatteryState(droneBattery);
                triggerBatteryStateChange();
                break;

            case AttributeEvent.GPS_POSITION:
            case AttributeEvent.GPS_FIX:
            case AttributeEvent.GPS_COUNT:
            case AttributeEvent.WARNING_NO_GPS:
                Log.d(getClass().getCanonicalName(), "GPS_POSITION / GPS_FIX / GPS_COUNT / WARNING_NO_GPS");

                gpsState = DroneStateMapper.getGPSState((Gps) drone.getAttribute(AttributeType.GPS));
                triggerGPSStateChange();
                break;

            default:
                break;
        }
    }

    private void clearDroneData() {
        droneState = new DroneState();
        gpsState = new GPSState();
        batteryState = new BatteryState();

        triggerDroneStateChange();
        triggerGPSStateChange();
        triggerBatteryStateChange();
    }


    public void takeOff() {
        DroneStateApi.setVehicleMode(drone, VehicleMode.COPTER_GUIDED);
        DroneStateApi.arm(drone, true);
        takeoffWhenArmed = true;
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

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }
}

