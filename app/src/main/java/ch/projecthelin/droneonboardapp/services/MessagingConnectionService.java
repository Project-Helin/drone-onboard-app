package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
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

@Singleton
public class MessagingConnectionService implements ConnectionListener{

    public enum QueueName {
        SERVER_TO_DRONE
    }

    public enum ConnectionState {
        DISCONNECTED, CONNECTED, RECONNECTING
    }

    public static final String RMQ_REMOTE_SERVER_ADDR = "192.168.56.101:5672";
    public static final String RMQ_LOCAL_SERVER_ADDR = "152.96.238.77:5672";

    public ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private Channel channel;
    private Connection connection;

    private List<MessagingListener> listeners = new ArrayList<>();



    @Inject
    public MessagingConnectionService() {}


    public void connect(String hostAddress, String serverAddress){
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
                        Consumer consumer = createConsumer();

                        channel.basicConsume(QueueName.SERVER_TO_DRONE.name(), true, consumer);


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
        try {
            Log.d("Send message ", message);
            channel.basicPublish("", QueueName.SERVER_TO_DRONE.name(), null, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    listener.onMessageReceived(message);
                }
                Log.d("message", message);
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


}
