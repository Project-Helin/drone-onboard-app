package ch.projecthelin.droneonboardapp.services;

import android.os.Bundle;
import ch.helin.messages.dto.state.DroneState;
import ch.projecthelin.droneonboardapp.di.DaggerTestAppComponent;
import ch.projecthelin.droneonboardapp.di.TestAppModule;
import ch.projecthelin.droneonboardapp.listeners.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.listeners.MissionListener;
import ch.projecthelin.droneonboardapp.mappers.DroneStateMapper;
import ch.projecthelin.droneonboardapp.mappers.RouteMissionMapper;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.model.action.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DroneConnectionServiceTest {

    private DroneConnectionService service;
    private DroneConnectionListener droneConnectionListener;
    private MissionListener missionListener;

    ControlTower tower;
    Drone drone;

    @Before
    public void setupServiceAndListener() {
        tower = mock(ControlTower.class);
        drone = mock(Drone.class);
        TestAppModule module = new TestAppModule();
        module.setControlTower(tower);
        module.setDrone(drone);

        DaggerTestAppComponent.builder()
                .testAppModule(module)
                .build();

        droneConnectionListener = mock(DroneConnectionListener.class);
        missionListener = mock(MissionListener.class);
        service = new DroneConnectionService(tower, drone, new RouteMissionMapper(), new DroneStateMapper());
    }

    @Test
    public void changedGPSStateTest() throws Exception {
        Gps gps = new Gps();
        gps.setFixType(3);
        gps.setSatCount(10);
        gps.setPosition(new LatLong(3.2, 4.3));

        when(drone.getAttribute(AttributeType.GPS)).thenReturn(gps);

        service.addConnectionListener(droneConnectionListener);
        service.onDroneEvent(AttributeEvent.WARNING_NO_GPS, new Bundle());

        verify(droneConnectionListener).onGpsStateChange(new DroneStateMapper().getGPSState(gps));
    }

    @Test
    public void changedConnectionStateTest() throws Exception {
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

        DroneState droneState = new DroneStateMapper().getDroneState(drone);
        droneState.setIsConnected(true);

        verify(droneConnectionListener).onDroneStateChange(droneState);
    }

    @Test
    public void missionTest() {
        int stabilizeModeAndArm = 2;
        int guidedModeAndTakeoff = 2;
        int autoMode = 1;

        //START MISSION
        service.startMission();

        //verify drone was set to stabilize mode and armed
        //can't test with real params because equals is not implemented on Action
        verify(drone, times(stabilizeModeAndArm)).performAsyncAction(any(Action.class));

        service.onDroneEvent(AttributeEvent.STATE_ARMING, new Bundle());

        //verify drone was set to guided mode and takeoff command was issued
        verify(drone, times(stabilizeModeAndArm + guidedModeAndTakeoff)).performAsyncAction(any(Action.class));

        Altitude altitude = new Altitude();
        altitude.setAltitude(10);
        altitude.setTargetAltitude(10);
        when(drone.getAttribute(AttributeType.ALTITUDE)).thenReturn(altitude);

        service.onDroneEvent(AttributeEvent.ALTITUDE_UPDATED, new Bundle());

        //verify drone goes to auto mode after reaching needed altitude
        verify(drone, times(stabilizeModeAndArm + guidedModeAndTakeoff + autoMode)).performAsyncAction(any(Action.class));

        //END MISSION
        service.setMissionListener(missionListener);

        Altitude landedAltitude = new Altitude();
        landedAltitude.setAltitude(0.5);
        when(drone.getAttribute(AttributeType.ALTITUDE)).thenReturn(landedAltitude);

        service.onDroneEvent(AttributeEvent.ALTITUDE_UPDATED, new Bundle());

        //verify mission was finished after landing
        verify(missionListener).onMissionFinished();
    }




}