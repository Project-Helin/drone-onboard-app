package ch.projecthelin.droneonboardapp.listeners;

import ch.helin.messages.dto.message.DroneDtoMessage;

public interface DroneAttributeUpdateReceiver {

    void onDroneAttributeUpdate(DroneDtoMessage droneDtoMessage);

}
