package ch.projecthelin.droneonboardapp.listeners;

import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsState;

public interface DroneConnectionListener {
    void onDroneStateChange(DroneState state);

    void onGpsStateChange(GpsState state);

    void onBatteryStateChange(BatteryState state);
}
