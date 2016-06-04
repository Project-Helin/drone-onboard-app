package ch.projecthelin.droneonboardapp.services;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.message.DroneActiveState;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import ch.helin.messages.dto.message.DroneDto;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.projecthelin.droneonboardapp.di.DaggerTestAppComponent;
import ch.projecthelin.droneonboardapp.di.TestAppModule;
import ch.projecthelin.droneonboardapp.listeners.DroneAttributeUpdateReceiver;
import ch.projecthelin.droneonboardapp.listeners.MessageReceiver;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessagingConnectionServiceTest {

    private MessageReceiver messageReceiver;
    private DroneAttributeUpdateReceiver droneMessageReceiver;
    private MessagingConnectionService service;
    private JsonBasedMessageConverter messageConverter = new JsonBasedMessageConverter();

    @Before
    public void setupServiceAndListener() {
        TestAppModule module = new TestAppModule();

        DaggerTestAppComponent.builder()
                .testAppModule(module)
                .build();

        messageReceiver = mock(MessageReceiver.class);
        droneMessageReceiver = mock(DroneAttributeUpdateReceiver.class);
        MessagingConnectionListener messagingConnectionListener = mock(MessagingConnectionListener.class);
        service = new MessagingConnectionService();
        service.addConnectionListener(messagingConnectionListener);
        service.addMissionMessageReceiver(messageReceiver);
        service.addDroneAttributeUpdateReceiver(droneMessageReceiver);

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
        DroneDto droneDto = new DroneDto();
        droneDto.setActive(true);
        droneDto.setPayload(300);
        droneDto.setName("myTestDrone");

        DroneDtoMessage droneDtoMessage = new DroneDtoMessage();
        droneDtoMessage.setDroneDto(droneDto);

        String messageStr = messageConverter.parseMessageToString(droneDtoMessage);

        JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();
        Message message = jsonBasedMessageConverter.parseStringToMessage(messageStr);

        service.notifyDroneAttributeMessageReceivers(message);

        verify(droneMessageReceiver).onDroneAttributeUpdate(droneDtoMessage);
    }


}
