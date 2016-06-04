package ch.projecthelin.droneonboardapp.services;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.projecthelin.droneonboardapp.di.DaggerTestAppComponent;
import ch.projecthelin.droneonboardapp.di.TestAppModule;
import ch.projecthelin.droneonboardapp.listeners.MessageReceiver;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessagingConnectionServiceTest {

    private MessageReceiver messageReceiver;
    private MessagingConnectionService service;
    private JsonBasedMessageConverter messageConverter = new JsonBasedMessageConverter();

    @Before
    public void setupServiceAndListener() {
        TestAppModule module = new TestAppModule();

        DaggerTestAppComponent.builder()
                .testAppModule(module)
                .build();

        messageReceiver = mock(MessageReceiver.class);
        MessagingConnectionListener messagingConnectionListener = mock(MessagingConnectionListener.class);
        service = new MessagingConnectionService();
        service.addConnectionListener(messagingConnectionListener);
        service.addMissionMessageReceiver(messageReceiver);
    }

    @Test
    public void assignMissionMessageReceivingTest() {
        AssignMissionMessage assignMissionMessage = new AssignMissionMessage();
        MissionDto mission = new MissionDto();
        assignMissionMessage.setMission(mission);

        String messageStr = messageConverter.parseMessageToString(assignMissionMessage);

        JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();
        Message message = jsonBasedMessageConverter.parseStringToMessage(messageStr);

        service.notifyMissionMessageReceivers(message);

        verify(messageReceiver).onAssignMissionMessageReceived(assignMissionMessage);
    }

    @Test
    public void finalAssignMissionMessageReceivingTest() {
        FinalAssignMissionMessage finalAssignMissionMessage = new FinalAssignMissionMessage();
        MissionDto mission = new MissionDto();
        finalAssignMissionMessage.setMission(mission);

        String messageStr = messageConverter.parseMessageToString(finalAssignMissionMessage);

        JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();
        Message message = jsonBasedMessageConverter.parseStringToMessage(messageStr);

        service.notifyMissionMessageReceivers(message);

        verify(messageReceiver).onFinalAssignMissionMessageReceived(finalAssignMissionMessage);
    }

    @Test
    public void droneAttributeUpdateTest(){
        
    }


}
