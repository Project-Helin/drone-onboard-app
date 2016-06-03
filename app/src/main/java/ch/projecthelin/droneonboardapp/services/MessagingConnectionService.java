package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
import ch.helin.commons.ConnectionUtils;
import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.projecthelin.droneonboardapp.listeners.DroneAttributeUpdateReceiver;
import ch.projecthelin.droneonboardapp.listeners.MessageReceiver;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import com.rabbitmq.client.*;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicy;
import net.jodah.lyra.event.ConnectionListener;
import net.jodah.lyra.util.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Singleton
public class MessagingConnectionService implements ConnectionListener {

    private static final String RMQ_LOCAL_PORT = ":5672";
    private static final String RMQ_REMOTE_PORT = ":8080";

    private ConnectionState connectionState;
    private String droneToken;
    private volatile Channel channel;
    private volatile Connection connection;
    private List<MessagingConnectionListener> connectionListeners = new ArrayList<>();
    private List<MessageReceiver> missionMessageReceivers = new ArrayList<>();
    private List<DroneAttributeUpdateReceiver> droneAttributeUpdateReceivers = new ArrayList<>();
    private Queue<String> messagesToSend = new ConcurrentLinkedQueue<>();
    private MissionDto currentMission;
    private String serverIP;
    private String rabbitMqServerAddress;
    private boolean localConnection;

    private static final int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 10, TimeUnit.SECONDS, queue);

    @Inject
    public MessagingConnectionService() {
        connectionState = ConnectionState.DISCONNECTED;
    }

    public void connect() {

        try {
            Runnable createConnectionTask = new Runnable() {
                public void run() {
                    try {
                        Config config = new Config()
                                .withRecoveryPolicy(new RecoveryPolicy()
                                        .withBackoff(Duration.seconds(1), Duration.seconds(30))
                                        .withMaxAttempts(20))
                                .withConnectionListeners(MessagingConnectionService.this);


                        if (localConnection) {
                            rabbitMqServerAddress = serverIP + RMQ_LOCAL_PORT;
                        } else {
                            rabbitMqServerAddress = serverIP + RMQ_REMOTE_PORT;
                        }

                        ConnectionOptions options = new ConnectionOptions()
                                .withAddresses(rabbitMqServerAddress)
                                .withUsername("admin")
                                .withPassword("helin");

                        connection = Connections.create(options, config);
                        channel = connection.createChannel(1);

                        Consumer consumer = createConsumer();

                        channel.basicConsume(ConnectionUtils.getDroneSideConsumerQueueName(droneToken), true, consumer);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            threadPoolExecutor.execute(createConnectionTask);


        } catch (Exception e) {
            disconnect();
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        disconnect(connection);
        closeChannel(channel);
        connectionState = ConnectionState.DISCONNECTED;
        notifyConnectionListeners(ConnectionState.DISCONNECTED);
    }

    private void disconnect(Connection connection) {
        if (connection != null && channel.isOpen()) {
            try {
                connection.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void closeChannel(Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addConnectionListener(MessagingConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(MessagingConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private void notifyConnectionListeners(ConnectionState state) {
        for (MessagingConnectionListener listener : connectionListeners) {
            listener.onConnectionStateChanged(state);
        }
    }

    public void addMissionMessageReceiver(MessageReceiver listener) {
        missionMessageReceivers.add(listener);
    }

    public void removeMissionMessageReceiver(MessageReceiver listener) {
        missionMessageReceivers.remove(listener);
    }

    public void addDroneAttributeUpdateReceiver(DroneAttributeUpdateReceiver droneAttributesUpdateListener) {
        droneAttributeUpdateReceivers.add(droneAttributesUpdateListener);
    }

    public void removeDroneAttributeUpdateReceiver(DroneAttributeUpdateReceiver droneAttributesUpdateListener) {
        droneAttributeUpdateReceivers.remove(droneAttributesUpdateListener);
    }

    public void notifyMissionMessageReceivers(Message message) {
        try {
            for (MessageReceiver receiver : missionMessageReceivers) {
                switch (message.getPayloadType()) {
                    case AssignMission:
                        receiver.onAssignMissionMessageReceived((AssignMissionMessage) message);
                        break;
                    case FinalAssignMission:
                        receiver.onFinalAssignMissionMessageReceived((FinalAssignMissionMessage) message);
                        break;
                }
            }
        } catch (ClassCastException e) {
            Log.d("Error", "Could not cast to" + message.getPayloadType(), e);
        }
    }

    public void notifyDroneAttributeMessageReceivers(Message message) {
        try {
            for (DroneAttributeUpdateReceiver droneAttributeUpdateReceiver : droneAttributeUpdateReceivers) {
                droneAttributeUpdateReceiver.onDroneAttributeUpdate((DroneDtoMessage) message);
            }
        } catch (ClassCastException e) {
            Log.d("Error", "Could not cast to" + message.getPayloadType(), e);
        }
    }

    public void sendMessage(String message) {
        messagesToSend.add(message);

        //sending messages can block and therefore it should
        //run in its own thread. MessagesToSend uses Concurrent
        //Queue so it is save to spawn multiple threads
        Runnable sendMessagesTask = new Runnable() {
            public void run() {
                try {
                    if (connection.isOpen()) {
                        while (messagesToSend.peek() != null) {
                            String messageToSend = messagesToSend.peek();
                            Log.d("Send Message", messageToSend);
                            channel.basicPublish("", ConnectionUtils.getDroneSideProducerQueueName(droneToken), null, messageToSend.getBytes());
                            messagesToSend.remove();
                        }
                    }
                } catch (Exception e) {
                    Log.d(getClass().getCanonicalName(), "Message sent failed!");
                }
            }

        };

        threadPoolExecutor.execute(sendMessagesTask);
    }

    private Consumer createConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String messageAsString = new String(body, "UTF-8");

                JsonBasedMessageConverter messageConverter = new JsonBasedMessageConverter();
                Message message = messageConverter.parseStringToMessage(messageAsString);

                switch (message.getPayloadType()) {
                    case AssignMission:
                    case FinalAssignMission:
                        notifyMissionMessageReceivers(message);
                        break;
                    case DroneDto:
                        notifyDroneAttributeMessageReceivers(message);
                        break;
                    default:
                        Log.d(getClass().getCanonicalName(), "Message Type is neighter Mission nor DroneAttribute Type");
                }
            }
        };
    }

    @Override
    public void onChannelRecovery(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
        notifyConnectionListeners(connectionState);
    }

    @Override
    public void onCreate(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
        notifyConnectionListeners(connectionState);
    }

    @Override
    public void onCreateFailure(Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
        notifyConnectionListeners(connectionState);
    }

    @Override
    public void onRecovery(Connection connection) {
        connectionState = ConnectionState.RECONNECTING;
        notifyConnectionListeners(connectionState);
    }

    @Override
    public void onRecoveryFailure(Connection connection, Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
        notifyConnectionListeners(connectionState);
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setIsLocalConnection(boolean localConnection) {
        this.localConnection = localConnection;
    }

    public enum ConnectionState {
        DISCONNECTED, CONNECTED, RECONNECTING
    }

    public MissionDto getCurrentMission() {
        return currentMission;
    }

    public void setCurrentMission(MissionDto currentMission) {
        this.currentMission = currentMission;
    }

    public String getRabbitMqServerAddress() {
        return rabbitMqServerAddress;
    }

    public void setDroneToken(String droneToken) {
        this.droneToken = droneToken;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

}
