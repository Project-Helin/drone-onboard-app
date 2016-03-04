package ch.projecthelin.droneonboardapp.services;

import android.util.Log;
import ch.projecthelin.droneonboardapp.MessageListener;
import com.rabbitmq.client.*;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicy;
import net.jodah.lyra.util.Duration;

import java.io.IOException;

public class MessagingConnectionService {

    public enum QueueName {
        SERVER_TO_DRONE
    }


    private Channel channel;
    private Connection connection;
    private MessageListener listener;

    public MessagingConnectionService(MessageListener listener) {

        this.listener = listener;
        try {
            final Runnable r = new Runnable() {
                public void run() {
                    try {
                        Config config = new Config()
                                .withRecoveryPolicy(new RecoveryPolicy()
                                        .withBackoff(Duration.seconds(1), Duration.seconds(30))
                                        .withMaxAttempts(20));

                        ConnectionOptions options = new ConnectionOptions()
                                .withAddresses("192.168.57.1:5672")
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
                listener.onMessageReceived(message);
                Log.d("message", message);
            }
        };

    }


}
