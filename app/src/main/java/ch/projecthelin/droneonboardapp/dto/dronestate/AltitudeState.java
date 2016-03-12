package ch.projecthelin.droneonboardapp.dto.dronestate;

public class AltitudeState implements DroneState{

    private double altitude;
    private double targetAltitude;

    public AltitudeState(){}

    public AltitudeState(double altitude, double targetAltitude) {
        this.altitude = altitude;
        this.targetAltitude = targetAltitude;
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
