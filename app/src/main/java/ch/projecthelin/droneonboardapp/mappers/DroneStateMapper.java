package ch.projecthelin.droneonboardapp.mappers;

import android.util.Log;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.*;

import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsQuality;
import ch.helin.messages.dto.state.GpsState;

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


    public static GpsState getGPSState(Gps gps) {
        GpsState gpsState = new GpsState();

        if (gps != null) {
            GpsQuality gpsQuality = GpsQuality.fromIndexOrReturnNoFix(gps.getFixType());

            gpsState.setFixType(gpsQuality);
            gpsState.setSatellitesCount(gps.getSatellitesCount());
            if (gps.getPosition() != null) {
                gpsState.setPosLon(gps.getPosition().getLongitude());
                gpsState.setPosLat(gps.getPosition().getLatitude());
            }
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
