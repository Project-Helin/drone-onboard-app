package ch.projecthelin.droneonboardapp.dto.dronestate;

public class SpeedState implements DroneState {
    private double verticalSpeed; // m/s
    private double groundSpeed; // m/s
    private double airSpeed; // m/s

    public SpeedState(){}

    public SpeedState(double verticalSpeed, double groundSpeed, double airSpeed) {
        this.verticalSpeed = verticalSpeed;
        this.groundSpeed = groundSpeed;
        this.airSpeed = airSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public void setAirSpeed(double airSpeed) {
        this.airSpeed = airSpeed;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public double getAirSpeed() {
        return airSpeed;
    }


}
