package ch.projecthelin.droneonboardapp;

import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

public interface MessagingListener {
    void onMessageReceived(String message);
    void onConnectionStateChanged(MessagingConnectionService.ConnectionState state);
}
