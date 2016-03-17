package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
import ch.projecthelin.droneonboardapp.MessageListener;
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

@Singleton
public class MessagingConnectionService implements ConnectionListener{

    public enum QueueName {
        SERVER_TO_DRONE
    }

    public enum ConnectionState {
        DISCONNECTED, CONNECTED, RECONNECTING
    }

    public static final String RMQ_REMOTE_SERVER_ADDR = "151.80.44.117:5672";
    public static final String RMQ_LOCAL_SERVER_ADDR = "192.168.57.1:5672";

    public ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private Channel channel;
    private Connection connection;

    private List<MessageListener> listeners = new ArrayList<>();



    @Inject
    public MessagingConnectionService() {}

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

                        ConnectionOptions options = new ConnectionOptions()
                                .withAddresses(RMQ_LOCAL_SERVER_ADDR)
                                .withUsername("admin").
                                        withPassword("helin");

                        connection = Connections.create(options, config);
                        channel = connection.createChannel(1);
                        Log.d("Messaging", "connected");
                        Consumer consumer = createConsumer();

                        channel.basicConsume(QueueName.SERVER_TO_DRONE.name(), true, consumer);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            new Thread(r).start();

        } catch (Exception e) {
            closeConnection();
            throw new RuntimeException(e);
        }
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    public void closeConnection() {
        closeConnection(connection);
        closeChannel(channel);
    }

    public void sendMessage(String message) {
        try {
            Log.d("Send message ", message);
            channel.basicPublish("", QueueName.SERVER_TO_DRONE.name(), null, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection(Connection connection) {
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
                for (MessageListener listener : listeners) {
                    listener.onMessageReceived(message);
                }
                Log.d("message", message);
            }
        };

    }

    @Override
    public void onChannelRecovery(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
    }

    @Override
    public void onCreate(Connection connection) {
        connectionState = ConnectionState.CONNECTED;
    }

    @Override
    public void onCreateFailure(Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public void onRecovery(Connection connection) {
        connectionState = ConnectionState.RECONNECTING;
    }

    @Override
    public void onRecoveryFailure(Connection connection, Throwable failure) {
        connectionState = ConnectionState.DISCONNECTED;
    }


}
