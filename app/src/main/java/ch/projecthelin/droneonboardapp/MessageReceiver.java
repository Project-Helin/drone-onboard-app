package ch.projecthelin.droneonboardapp;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;

public interface MessageReceiver {
    void onAssignMissionMessageReceived(AssignMissionMessage message);
}
