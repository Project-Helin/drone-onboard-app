package ch.projecthelin.droneonboardapp;

import android.content.Context;
import android.os.Bundle;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.mappers.DroneStateMapper;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DroneConnectionServiceTest {


    @Test
    public void ChangedGPSState() throws Exception {
        Drone drone = mock(Drone.class);
        ControlTower tower = mock(ControlTower.class);
        DroneConnectionService service = DroneConnectionService.getInstance(tower, drone);
        DroneConnectionListener droneConnectionListener = mock(DroneConnectionListener.class);

        Gps gps = new Gps();
        gps.setFixType(3);
        gps.setSatCount(10);

        when(drone.getAttribute(AttributeType.GPS)).thenReturn(gps);

        service.addConnectionListener(droneConnectionListener);
        service.onDroneEvent(AttributeEvent.WARNING_NO_GPS, new Bundle());

        verify(droneConnectionListener).onGPSStateChange(DroneStateMapper.getGPSState(gps));
    }

    @Test
    public void ChangedConnectionState() throws Exception {
        Drone drone = mock(Drone.class);
        ControlTower tower = mock(ControlTower.class);
        DroneConnectionService service = DroneConnectionService.getInstance(tower, drone);
        DroneConnectionListener droneConnectionListener = mock(DroneConnectionListener.class);

        Altitude altitude = new Altitude();
        altitude.setAltitude(100);
        altitude.setTargetAltitude(150);

        Speed speed = new Speed();
        speed.setGroundSpeed(5);
        speed.setVerticalSpeed(1);

        Type type = new Type();
        type.setFirmware(Type.Firmware.ARDU_COPTER);

        when(drone.getAttribute(AttributeType.ALTITUDE)).thenReturn(altitude);
        when(drone.getAttribute(AttributeType.SPEED)).thenReturn(speed);
        when(drone.getAttribute(AttributeType.TYPE)).thenReturn(type);

        service.addConnectionListener(droneConnectionListener);

        service.onDroneEvent(AttributeEvent.STATE_CONNECTED, new Bundle());

        verify(droneConnectionListener).onDroneStateChange(DroneStateMapper.getDroneState(drone));
    }
}