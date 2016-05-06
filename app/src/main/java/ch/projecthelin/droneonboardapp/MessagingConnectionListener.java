package ch.projecthelin.droneonboardapp;

import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

public interface MessagingConnectionListener {
    void onConnectionStateChanged(MessagingConnectionService.ConnectionState state);
}
