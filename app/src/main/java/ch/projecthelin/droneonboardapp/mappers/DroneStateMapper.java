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
        droneState.setVerticalSpeed(speed.getVerticalSpeed());
        droneState.setGroundSpeed(speed.getGroundSpeed());

        Altitude altitude = drone.getAttribute(AttributeType.ALTITUDE);
        droneState.setTargetAltitude(altitude.getTargetAltitude());
        droneState.setTargetAltitude(altitude.getAltitude());

        Type type = drone.getAttribute(AttributeType.TYPE);
        droneState.setFirmeware(type.getFirmware().getLabel());

        return droneState;
    }


    public static GPSState getGPSState(Gps gps) {
        GPSState gpsState = new GPSState();

        gpsState.setFixType(gps.getFixType());
        gpsState.setSatellitesCount(gps.getSatellitesCount());
        gpsState.setPosition(gps.getPosition());

        return gpsState;
    }
}
