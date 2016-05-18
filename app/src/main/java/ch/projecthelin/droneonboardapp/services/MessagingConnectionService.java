package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
import ch.helin.messages.commons.ConnectionUtils;
import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.MissionDto;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.missionMessage.AssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.projecthelin.droneonboardapp.MessagingConnectionListener;
import ch.projecthelin.droneonboardapp.MessageReceiver;
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

@Singleton
public class MessagingConnectionService implements ConnectionListener {

    public static final String RMQ_LOCAL_PORT = ":5672";
    public static final String RMQ_REMOTE_PORT = ":8080";

    public ConnectionState connectionState = ConnectionState.DISCONNECTED;

    private String droneToken;
    private Channel channel;
    private Connection connection;
    private List<MessagingConnectionListener> connectionListeners = new ArrayList<>();
    private List<MessageReceiver> messageReceivers = new ArrayList<>();
    private Queue<String> messagesToSend = new ConcurrentLinkedQueue<>();
    private MissionDto currentMission;
    private String serverIP;
    private String rabbitMqServerAddress;
    private boolean localConnection;

    public String getRabbitMqServerAddress() {
        return rabbitMqServerAddress;
    }

    @Inject
    public MessagingConnectionService() {
    }

    public void setDroneToken(String droneToken) {
        this.droneToken = droneToken;
    }

    public void connect() {
        try {
            final Runnable r = new Runnable() {
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
            new Thread(r).start();

        } catch (Exception e) {
            disconnect();
            throw new RuntimeException(e);
        }
    }

    public void addConnectionListener(MessagingConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(MessagingConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public void notifyConnectionListeners(ConnectionState state) {
        for (MessagingConnectionListener listener : connectionListeners) {
            listener.onConnectionStateChanged(state);
        }
    }

    public void addMessageReceiver(MessageReceiver listener) {
        messageReceivers.add(listener);
    }

    public void removeMessageReceiver(MessageReceiver listener) {
        messageReceivers.remove(listener);
    }

    public void notifyMessageReceivers(String messageAsString) {
        JsonBasedMessageConverter messageConverter = new JsonBasedMessageConverter();
        Message message = messageConverter.parseStringToMessage(messageAsString);

        for (MessageReceiver receiver : messageReceivers) {
            switch(message.getPayloadType()) {
                case AssignMission:
                    receiver.onAssignMissionMessageReceived((AssignMissionMessage) message);
                    break;
                case FinalAssignMission:
                    receiver.onFinalAssignMissionMessageReceived((FinalAssignMissionMessage) message);
            }
        }
    }

    public void disconnect() {
        disconnect(connection);
        closeChannel(channel);
        connectionState = ConnectionState.DISCONNECTED;
        notifyConnectionListeners(ConnectionState.DISCONNECTED);
    }

    public void sendMessage(String message) {
        messagesToSend.add(message);
        try {
            if (connection.isOpen()) {

                while (messagesToSend.peek() != null) {
                    String messageToSend = messagesToSend.remove();
                    Log.d("Send Message", messageToSend);
                    channel.basicPublish("", ConnectionUtils.getDroneSideProducerQueueName(droneToken), null, messageToSend.getBytes());
                }
            }

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
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

    public Consumer createConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String messageAsString = new String(body, "UTF-8");

                notifyMessageReceivers(messageAsString);
            }
        };

    }

    public void setCurrentMission(MissionDto currentMission) {
        this.currentMission = currentMission;
    }

    public MissionDto getCurrentMission() {
        return currentMission;
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


}
