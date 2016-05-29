package mappers;

import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsQuality;
import ch.helin.messages.dto.state.GpsState;
import ch.projecthelin.droneonboardapp.mappers.DroneStateMapper;
import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.*;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DroneStateMapperTest {

    @Test
    public void getDroneStateTest () {

        Drone drone = mock(Drone.class);

        Speed speed = new Speed(100, 49, 5);
        when(drone.getAttribute(AttributeType.SPEED)).thenReturn(speed);

        Altitude altitude = new Altitude(23, 55);
        when(drone.getAttribute(AttributeType.ALTITUDE)).thenReturn(altitude);

        Type type = new Type(1, "4.5");
        when(drone.getAttribute(AttributeType.TYPE)).thenReturn(type);


        DroneState droneState = DroneStateMapper.getDroneState(drone);

        assertThat(droneState.getVerticalSpeed()).isEqualTo(speed.getVerticalSpeed());
        assertThat(droneState.getGroundSpeed()).isEqualTo(speed.getGroundSpeed());

        assertThat(droneState.getAltitude()).isEqualTo(altitude.getAltitude());
        assertThat(droneState.getTargetAltitude()).isEqualTo(altitude.getTargetAltitude());

        assertThat(droneState.getFirmware()).isEqualTo(type.getFirmware().getLabel());
    }

    @Test
    public void getGPSStateTest() {
        Gps gps = new Gps(47.234343, 8.341234, 4.5, 8, 3);

        GpsState gpsState = DroneStateMapper.getGPSState(gps);

        assertThat(gpsState.getPosLat()).isEqualTo(gps.getPosition().getLatitude());
        assertThat(gpsState.getPosLon()).isEqualTo(gps.getPosition().getLongitude());
        assertThat(gpsState.getFixType()).isEqualTo(GpsQuality.fromIndexOrReturnNoFix(gps.getFixType()));
        assertThat(gpsState.getSatellitesCount()).isEqualTo(gps.getSatellitesCount());
    }

    @Test
    public void getBatteryStateTest() {
        Battery battery = new Battery(12.4, 55, 5, 2.45);

        BatteryState batteryState = DroneStateMapper.getBatteryState(battery);

        assertThat(batteryState.getVoltage()).isEqualTo(battery.getBatteryVoltage());
        assertThat(batteryState.getCurrent()).isEqualTo(battery.getBatteryCurrent());
        assertThat(batteryState.getDischarge()).isEqualTo(battery.getBatteryDischarge());
        assertThat(batteryState.getRemain()).isEqualTo(battery.getBatteryRemain());
    }

}
