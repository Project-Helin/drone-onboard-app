package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
import ch.helin.messages.commons.ConnectionUtils;
import ch.helin.messages.dto.message.Message;
import ch.projecthelin.droneonboardapp.MessagingListener;
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

    public static final String RMQ_REMOTE_SERVER_ADDR = "151.80.44.117:8080";
    public static final String RMQ_LOCAL_SERVER_ADDR = "192.168.58.1:5672";

    public ConnectionState connectionState = ConnectionState.DISCONNECTED;

    private String droneToken;
    private Channel channel;
    private Connection connection;
    private List<MessagingListener> listeners = new ArrayList<>();
    private Queue<String> messagesToSend = new ConcurrentLinkedQueue<>();

    @Inject
    public MessagingConnectionService() {
    }

    public void setDroneToken(String droneToken) {
        this.droneToken = droneToken;
    }

    public void connect(final String hostAddress, final String serverAddress) {
        try {
            final Runnable r = new Runnable() {
                public void run() {
                    try {
                        Config config = new Config()
                                .withRecoveryPolicy(new RecoveryPolicy()
                                        .withBackoff(Duration.seconds(1), Duration.seconds(30))
                                        .withMaxAttempts(20))
                                .withConnectionListeners(MessagingConnectionService.this);

                        ConnectionOptions options = new ConnectionOptions()
                                .withAddresses(hostAddress)
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

    public void connect() {
        connect(RMQ_LOCAL_SERVER_ADDR, RMQ_REMOTE_SERVER_ADDR);
    }

    public void addListener(MessagingListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessagingListener listener) {
        listeners.remove(listener);
    }

    public void notifyListenersConnectionState(ConnectionState state) {
        for (MessagingListener listener : listeners) {
            listener.onConnectionStateChanged(state);
        }
    }

    public void disconnect() {
        disconnect(connection);
        closeChannel(channel);
        connectionState = ConnectionState.DISCONNECTED;
        notifyListenersConnectionState(ConnectionState.DISCONNECTED);
    }

    public void sendMessage(String message) {
        messagesToSend.add(message);
        try {
            if (connection.isOpen()) {

                while(messagesToSend.peek() != null) {
                    String messageToSend = messagesToSend.remove();
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
                String message = new String(body, "UTF-8");
                for (MessagingListener listener : listeners) {
                    //listener.onMessageReceived(message);
                }
                //Log.d("message", message);
            }
        };

    }

    @Override
    public void onChannelRecovery(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
        notifyListenersConnectionState(connectionState);
    }

    @Override
    public void onCreate(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
        notifyListenersConnectionState(connectionState);
    }

    @Override
    public void onCreateFailure(Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
        notifyListenersConnectionState(connectionState);
    }

    @Override
    public void onRecovery(Connection connection) {
        connectionState = ConnectionState.RECONNECTING;
        notifyListenersConnectionState(connectionState);
    }

    @Override
    public void onRecoveryFailure(Connection connection, Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
        notifyListenersConnectionState(connectionState);
    }

    public enum ConnectionState {
        DISCONNECTED, CONNECTED, RECONNECTING
    }


}
