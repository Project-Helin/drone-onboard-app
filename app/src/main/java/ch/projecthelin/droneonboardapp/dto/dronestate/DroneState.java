package ch.projecthelin.droneonboardapp.dto.dronestate;

public class DroneState {

    private boolean isConnected;
    private boolean isGPSconnected;

    private double verticalSpeed; // m/s
    private double groundSpeed; // m/s

    private double altitude;
    private double targetAltitude;
    private String firmware;


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

    public void setFirmeware(String firmware) {
        this.firmware = firmware;
    }

    public String getFirmware(){
        return this.firmware;
    }

    @Override
    public String toString() {
        return "DroneState{" +
                "isConnected=" + isConnected +
                ", isGPSconnected=" + isGPSconnected +
                ", verticalSpeed=" + verticalSpeed +
                ", groundSpeed=" + groundSpeed +
                ", altitude=" + altitude +
                ", targetAltitude=" + targetAltitude +
                ", firmware='" + firmware + '\'' +
                '}';
    }
}
