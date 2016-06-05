package ch.projecthelin.droneonboardapp.mappers;

import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsQuality;
import ch.helin.messages.dto.state.GpsState;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.*;

import javax.inject.Inject;

public class DroneStateMapper {

    @Inject
    public DroneStateMapper() {
    }

    public DroneState getDroneState(Drone drone) {
        DroneState droneState = new DroneState();

        Speed speed = drone.getAttribute(AttributeType.SPEED);
        if (speed != null) {
            droneState.setVerticalSpeed(speed.getVerticalSpeed());
            droneState.setGroundSpeed(speed.getGroundSpeed());
        }

        Altitude altitude = drone.getAttribute(AttributeType.ALTITUDE);
        if (altitude != null) {
            droneState.setTargetAltitude(altitude.getTargetAltitude());
            droneState.setAltitude(altitude.getAltitude());
        }

        Type type = drone.getAttribute(AttributeType.TYPE);
        if (type != null && type.getFirmware() != null) {
            droneState.setFirmeware(type.getFirmware().getLabel());
        }

        return droneState;
    }


    public GpsState getGPSState(Gps gps) {
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


    public BatteryState getBatteryState(Battery droneBattery) {
        BatteryState batteryState = new BatteryState();

        if (droneBattery != null) {
            batteryState.setVoltage(droneBattery.getBatteryVoltage());
            batteryState.setCurrent(droneBattery.getBatteryCurrent());

            // because the batteryDischarge is of type Double but
            // all the other values are primitive (no idea why)
            Double discharge = droneBattery.getBatteryDischarge();
            batteryState.setDischarge(discharge == null ? 0 : discharge.doubleValue());

            batteryState.setRemain(droneBattery.getBatteryRemain());
        }

        return batteryState;
    }
}
