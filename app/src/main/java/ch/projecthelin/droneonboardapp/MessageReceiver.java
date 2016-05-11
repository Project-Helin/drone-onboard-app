package ch.projecthelin.droneonboardapp;

import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;

public interface MessageReceiver {
    void onAssignMissionMessageReceived(AssignMissionMessage message);
    void onFinalAssignMissionMessageReceived(FinalAssignMissionMessage message);
}
