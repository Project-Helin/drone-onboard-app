package ch.projecthelin.droneonboardapp.mappers;

import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.Type;

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
}
