package ch.projecthelin.droneonboardapp.mappers;

import android.util.Log;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.*;
import org.mockito.internal.matchers.Null;

public class DroneStateMapper {

    public static DroneState getDroneState(Drone drone) {
        DroneState droneState = new DroneState();

        Speed speed = drone.getAttribute(AttributeType.SPEED);
        if (speed != null) {
            droneState.setVerticalSpeed(speed.getVerticalSpeed());
            droneState.setGroundSpeed(speed.getGroundSpeed());
        }

        Altitude altitude = drone.getAttribute(AttributeType.ALTITUDE);
        if (altitude != null) {
            droneState.setTargetAltitude(altitude.getTargetAltitude());
            droneState.setTargetAltitude(altitude.getAltitude());
        }

        Type type = drone.getAttribute(AttributeType.TYPE);
        if (type != null && type.getFirmware() != null) {
            droneState.setFirmeware(type.getFirmware().getLabel());
        }

        return droneState;
    }


    public static GPSState getGPSState(Gps gps) {
        GPSState gpsState = new GPSState();

        if (gps != null) {
            gpsState.setFixType(gps.getFixType());
            gpsState.setSatellitesCount(gps.getSatellitesCount());
            gpsState.setPosition(gps.getPosition());
        }

        return gpsState;
    }


    public static BatteryState getBatteryState(Battery droneBattery) {
        BatteryState batteryState = new BatteryState();
        try {
            if (droneBattery != null) {
                batteryState.setVoltage(droneBattery.getBatteryVoltage());
                batteryState.setCurrent(droneBattery.getBatteryCurrent());
                batteryState.setDischarge(droneBattery.getBatteryDischarge());
                batteryState.setRemain(droneBattery.getBatteryRemain());
            }
        } catch (NullPointerException e) {
            Log.d("DroneStateMapper", "wait for BatteryState");
        }

        return batteryState;
    }
}
