package ch.projecthelin.droneonboardapp.listeners;

import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

public interface MessagingConnectionListener {
    void onConnectionStateChanged(MessagingConnectionService.ConnectionState state);
}
