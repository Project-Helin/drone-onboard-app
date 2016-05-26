import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import ch.helin.messages.dto.way.RouteDto;
import ch.helin.messages.dto.way.Waypoint;
import ch.projecthelin.droneonboardapp.mappers.RouteMissionMapper;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.mission.Mission;
import com.o3dr.services.android.lib.drone.mission.MissionItemType;
import com.o3dr.services.android.lib.drone.mission.item.command.SetServo;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;

public class RouteMissionMapperTest {

    private RouteDto setupRoute() {
        RouteDto routeDto = new RouteDto();

        ArrayList<Waypoint> wayPoints = new ArrayList<>();
        Waypoint wp1 = new Waypoint();
        wp1.setAction(Action.FLY);
        wp1.setPosition(new Position(47.223343, 8.818480, 20));
        wayPoints.add(wp1);

        Waypoint wp2 = new Waypoint();
        wp1.setAction(Action.FLY);
        wp2.setPosition(new Position(47.222614, 8.817452, 5));
        wayPoints.add(wp2);

        Waypoint wp3 = new Waypoint();
        wp3.setAction(Action.DROP);
        wp3.setPosition(new Position(47.223510, 8.815553, 50));
        wayPoints.add(wp3);

        Waypoint wp4 = new Waypoint();
        wp1.setAction(Action.FLY);
        wp4.setPosition(new Position(47.224209, 8.818933, 15));
        wayPoints.add(wp4);

        routeDto.setWayPoints(wayPoints);
        return routeDto;
    }

    @Test
    public void shouldDropAfterCorrectWaypoint() {
        RouteDto routeDto = setupRoute();

        Mission mission = new RouteMissionMapper().convertToMission(routeDto, servoChannel, servoPWM);

        SetServo dropAction = (SetServo) mission.getMissionItem(3);
        assertThat(dropAction.getType()).isEqualTo(MissionItemType.SET_SERVO);
    }

    @Test
    public void shouldAddWaypointsInCorrectOrder () {
        RouteDto routeDto = setupRoute();

        Mission mission = new RouteMissionMapper().convertToMission(routeDto, servoChannel, servoPWM);

        int dropWaypoint = 1;
        int landWaypoint = 1;

        assertThat(mission.getMissionItems().size()).isEqualTo(routeDto.getWayPoints().size() + dropWaypoint + landWaypoint);

        com.o3dr.services.android.lib.drone.mission.item.spatial.Waypoint firstWaypoint = (com.o3dr.services.android.lib.drone.mission.item.spatial.Waypoint) mission.getMissionItem(0);
        assertThat(firstWaypoint.getCoordinate()).isEqualTo(new LatLongAlt(47.223343, 8.818480, 20));


        com.o3dr.services.android.lib.drone.mission.item.spatial.Waypoint lastWaypoint = (com.o3dr.services.android.lib.drone.mission.item.spatial.Waypoint) mission.getMissionItem(4);
        assertThat(lastWaypoint.getCoordinate()).isEqualTo(new LatLongAlt(47.224209, 8.818933, 15));

    }
}
