package ch.projecthelin.droneonboardapp.services;

import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;

public interface DroneConnectionListener {

    void onConnectionStateChange(DroneState state);

 //   void onBatteryState(BatteryState state);


}
