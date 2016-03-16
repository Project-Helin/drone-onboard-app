package ch.projecthelin.droneonboardapp.dto.dronestate;

public class DroneState {

    private boolean isConnected;
    private boolean isGPSconnected;
    private GPSState gpsState;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DroneState that = (DroneState) o;

        if (isConnected != that.isConnected) return false;
        if (isGPSconnected != that.isGPSconnected) return false;
        if (Double.compare(that.verticalSpeed, verticalSpeed) != 0) return false;
        if (Double.compare(that.groundSpeed, groundSpeed) != 0) return false;
        if (Double.compare(that.altitude, altitude) != 0) return false;
        if (Double.compare(that.targetAltitude, targetAltitude) != 0) return false;
        if (gpsState != null ? !gpsState.equals(that.gpsState) : that.gpsState != null) return false;
        return !(firmware != null ? !firmware.equals(that.firmware) : that.firmware != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (isConnected ? 1 : 0);
        result = 31 * result + (isGPSconnected ? 1 : 0);
        result = 31 * result + (gpsState != null ? gpsState.hashCode() : 0);
        temp = Double.doubleToLongBits(verticalSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(groundSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(targetAltitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (firmware != null ? firmware.hashCode() : 0);
        return result;
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

    public GPSState getGpsState() {
        return gpsState;
    }

    public void setGpsState(GPSState gpsState) {
        this.gpsState = gpsState;
    }
}
