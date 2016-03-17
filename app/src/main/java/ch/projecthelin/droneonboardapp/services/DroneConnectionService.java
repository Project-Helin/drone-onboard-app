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
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DroneConnectionService implements DroneListener, TowerListener {

    // remove later!
    public static final String TCP_SERVER_IP = "192.168.57.1";
    // public static final int BAUD_RATE_FOR_USB = 115200;
    public static final int TCP_SERVER_PORT = 5760;

    public ConnectionParameter connectionParameter;

    private static DroneConnectionService instance;

    private ControlTower controlTower;
    private Drone drone;
    private final Handler handler = new Handler();

    private List<DroneConnectionListener> connectionListeners = new ArrayList<>();
    private DroneState droneState = new DroneState();

    @Inject
    public DroneConnectionService(ControlTower controlTower, Drone drone) {
        this.controlTower = controlTower;
        this.drone = drone;
        this.controlTower.connect(this);
    }

    public void connect() {

        Bundle extraParams = new Bundle();
        extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, TCP_SERVER_IP);
        extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, TCP_SERVER_PORT);
        connectionParameter = new ConnectionParameter(ConnectionType.TYPE_TCP, extraParams, null);

        drone.connect(connectionParameter);
    }

    public void disconnect() {
        drone.disconnect();
    }

    public DroneState getDroneState() {
        return droneState;
    }

    public void addConnectionListener(DroneConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
    }

    public void triggerDroneStateChange(DroneState state) {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDroneStateChange(state);
        }
    }

    public void triggerGPSStateChange(GPSState state) {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onGPSStateChange(state);
        }
    }

    public void triggerBatteryStateChange(BatteryState state) {
        for (DroneConnectionListener connectionListener : connectionListeners) {
            connectionListener.onBatteryStateChange(state);
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
                this.triggerDroneStateChange(DroneStateMapper.getDroneState(drone));
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                Log.d(getClass().getCanonicalName(), "STATE_DISCONNECTED");
                this.triggerDroneStateChange(DroneStateMapper.getDroneState(drone));
                break;

            case AttributeEvent.STATE_UPDATED:
                Log.d(getClass().getCanonicalName(), "STATE_UPDATED");

                break;

            case AttributeEvent.STATE_ARMING:
                Log.d(getClass().getCanonicalName(), "STATE_ARMING");

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

                triggerDroneStateChange(state);

                break;

            case AttributeEvent.BATTERY_UPDATED:
                Log.d(getClass().getCanonicalName(), "BATTERY_UPDATED");

                Battery droneBattery = drone.getAttribute(AttributeType.BATTERY);
                BatteryState batteryState = new BatteryState(droneBattery.getBatteryVoltage(),
                        droneBattery.getBatteryCurrent(),
                        droneBattery.getBatteryDischarge(),
                        droneBattery.getBatteryRemain());

                triggerBatteryStateChange(batteryState);
                break;

            case AttributeEvent.GPS_POSITION:
            case AttributeEvent.GPS_FIX:
            case AttributeEvent.GPS_COUNT:
            case AttributeEvent.WARNING_NO_GPS:
                Log.d(getClass().getCanonicalName(), "GPS_POSITION / GPS_FIX / GPS_COUNT / WARNING_NO_GPS");

                GPSState gpsState = DroneStateMapper.getGPSState((Gps)drone.getAttribute(AttributeType.GPS));
                triggerGPSStateChange(gpsState);
                break;

            default:
                break;
        }
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


}

