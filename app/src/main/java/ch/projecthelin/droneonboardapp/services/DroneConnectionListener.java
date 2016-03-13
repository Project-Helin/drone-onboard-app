package ch.projecthelin.droneonboardapp.services;

import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;

public interface DroneConnectionListener {

    void onDroneStateChange(DroneState state);
    void onGPSStateChange(GPSState state);
    void onBatteryStateChange(BatteryState state);


}
