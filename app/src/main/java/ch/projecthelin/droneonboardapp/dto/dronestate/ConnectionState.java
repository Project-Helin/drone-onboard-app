package ch.projecthelin.droneonboardapp.dto.dronestate;


public class ConnectionState implements DroneState {

    private boolean isConnected;

    public ConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public String toString() {
        return "ConnectionState{" +
                "isConnected=" + isConnected +
                '}';
    }

}
