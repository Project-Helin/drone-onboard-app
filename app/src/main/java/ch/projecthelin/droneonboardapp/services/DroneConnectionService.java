package ch.projecthelin.droneonboardapp.services;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.Type;

import ch.projecthelin.droneonboardapp.dto.dronestate.AltitudeState;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;

public class DroneConnectionService implements DroneListener, TowerListener, DroneConnectionListener{

    // remove later!
    public static final String TCP_SERVER_IP = "192.168.56.1";
    // public static final int BAUD_RATE_FOR_USB = 115200;
    public static final int TCP_SERVER_PORT = 5760;

    public ConnectionParameter connectionParameter;

    private static DroneConnectionService instance;

    private DroneConnectionListener connectionListener;

    private static ControlTower controlTower;
    private static Drone drone;
    private final Handler handler = new Handler();

    private DroneConnectionService(Context applicationContext){
        controlTower = new ControlTower(applicationContext);
        drone = new Drone(applicationContext);

        controlTower.connect(this);

    }

    public void connect(){

        Bundle extraParams = new Bundle();
        extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, TCP_SERVER_IP);
        extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, TCP_SERVER_PORT);
        connectionParameter = new ConnectionParameter(ConnectionType.TYPE_TCP, extraParams, null);

        drone.connect(connectionParameter);
    }

    public void disconnect(){
        drone.disconnect();
    }

    public static DroneConnectionService getInstance(Context applicationContext) {
        if(instance == null){
            instance = new DroneConnectionService(applicationContext);
        }
        return instance;
    }    

    public String test(){
        return "Test";
    }


    public void addConnectionListener(DroneConnectionListener connectionListener){
        this.connectionListener = connectionListener;
    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {

    }

   // Map<String, StateMapper> stateNameToMapper;
    @Override
    public void onDroneEvent(String event, Bundle extras) {
        //Log.d(getClass().getCanonicalName(), event.toString());
     //   StateMapper mapper = stateNameToMapper.get(event);
      //  DroneState state = mapper.getState(drone);
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                Log.d(getClass().getCanonicalName(), "STATE_CONNECTED");
                Log.d(getClass().getCanonicalName(), AttributeEvent.STATE_CONNECTED.toString());

                //State state = drone.getAttribute(AttributeType.STATE);

              //  state
                //connectionListener.onConnectionStateChange(new ConnectionState(true));
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                Log.d(getClass().getCanonicalName(), "STATE_DISCONNECTED");
                //connectionListener.onConnectionStateChange(new ConnectionState(false));
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

            case AttributeEvent.TYPE_UPDATED:
                Log.d(getClass().getCanonicalName(), "TYPE_UPDATED");

                Type type = drone.getAttribute(AttributeType.TYPE);
                type.getFirmware().getLabel();
                break;

            case AttributeEvent.SPEED_UPDATED:
                Log.d(getClass().getCanonicalName(), "SPEED_UPDATED");

                Speed speed = drone.getAttribute(AttributeType.SPEED);
                //SpeedState speedState = new SpeedState(speed.getVerticalSpeed(), speed.getAirSpeed(), speed.getGroundSpeed());
                //connectionListener.onConnectionStateChange(speedState);

                break;

            case AttributeEvent.HOME_UPDATED:
                Log.d(getClass().getCanonicalName(), "HOME_UPDATED");
                break;

            case AttributeEvent.ALTITUDE_UPDATED:
                Log.d(getClass().getCanonicalName(), "ALTITUDE_UPDATED");
                Altitude altitude = drone.getAttribute(AttributeType.ALTITUDE);

             //   AltitudeState altitudeState = new AltitudeState(altitude.getAltitude(), altitude.getTargetAltitude());
                //connectionListener.onConnectionStateChange(altitudeState);
                break;

            case AttributeEvent.BATTERY_UPDATED:
                Log.d(getClass().getCanonicalName(), "BATTERY_UPDATED");

                Battery droneBattery = drone.getAttribute(AttributeType.BATTERY);
                BatteryState batteryState = new BatteryState(droneBattery.getBatteryVoltage(),
                        droneBattery.getBatteryCurrent(),
                        droneBattery.getBatteryDischarge(),
                        droneBattery.getBatteryRemain());

                connectionListener.onBatteryStateChange(batteryState);
                break;

            case AttributeEvent.GPS_POSITION:
            case AttributeEvent.GPS_FIX:
            case AttributeEvent.GPS_COUNT:
            case AttributeEvent.WARNING_NO_GPS:
                Log.d(getClass().getCanonicalName(), "GPS_POSITION / GPS_FIX / GPS_COUNT / WARNING_NO_GPS");

                Gps droneGPS = drone.getAttribute(AttributeType.GPS);
                GPSState gpsState = new GPSState(droneGPS.getFixStatus(),
                        droneGPS.getSatellitesCount(),droneGPS.getPosition().getLatitude(),
                        droneGPS.getPosition().getLongitude());

                connectionListener.onGPSStateChange(gpsState);
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

    @Override
    public void onDroneStateChange(DroneState state) {

    }

    @Override
    public void onGPSStateChange(GPSState state) {

    }

    @Override
    public void onBatteryStateChange(BatteryState state) {

    }
}

