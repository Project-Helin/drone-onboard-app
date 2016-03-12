package ch.projecthelin.droneonboardapp.services;

public class DroneState {

    private boolean isConnected;

    private BatteryState batteryState;
    private GPSState gpsState;

    public DroneState(){

    }


    public void setIsConnected(boolean isConnected){
        this.isConnected = isConnected;
    }
    public boolean getIsConnected(){
        return isConnected;
    }

    public void setBatteryState(BatteryState batteryState){
        this.batteryState = batteryState;
    }
    public BatteryState getBatteryState(){
        return this.batteryState;
    }

    public void setGPSState(GPSState gpsState){
        this.gpsState = gpsState;
    }
    public GPSState getGPSState(){
        return this.gpsState;
    }

    @Override
    public String toString() {
        return "DroneState{" +
                "isConnected=" + isConnected +
                ", batteryState=" + batteryState +
                ", gpsState=" + gpsState +
                '}';
    }
}

