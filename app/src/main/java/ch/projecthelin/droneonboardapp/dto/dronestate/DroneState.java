package ch.projecthelin.droneonboardapp.dto.dronestate;

public class DroneState {

    private String firmware;
    private boolean isConnected;
    private boolean isGPSconnected;

    private double verticalSpeed; // m/s
    private double groundSpeed; // m/s

    private double altitude;
    private double targetAltitude;


    public boolean isConnected() {
        return isConnected;
    }
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getTargetAltitude() {
        return targetAltitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setTargetAltitude(double targetAltitude) {
        this.targetAltitude = targetAltitude;
    }


}
