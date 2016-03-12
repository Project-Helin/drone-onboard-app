package ch.projecthelin.droneonboardapp.services;

public class DroneState {

    private boolean isConnected;

    private GPSState gpsState;

    public void setGPSState(GPSState gpsState){
        this.gpsState = gpsState;
    }

    public GPSState getGPSState(){
        return this.gpsState;
    }

    private String gpsStatus;

    public DroneState(){

    }

    public void setIsConnected(boolean isConnected){
        this.isConnected = isConnected;
    }

    public boolean getIsConnected(){
        return isConnected;
    }

    @Override
    public String toString() {
        return "DroneState{" +
                "isConnected=" + isConnected +
                ", gpsState=" + gpsState +
                ", gpsStatus='" + gpsStatus + '\'' +
                '}';
    }
}

