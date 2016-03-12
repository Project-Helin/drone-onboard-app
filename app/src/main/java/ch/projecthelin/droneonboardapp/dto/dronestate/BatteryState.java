package ch.projecthelin.droneonboardapp.dto.dronestate;

public class BatteryState implements DroneState{

    private double voltage;
    private double remain;
    private double current;
    private double discharge;

    public BatteryState(double voltage, double current, double discharge, double remain) {
        this.voltage = voltage;
        this.remain = remain;
        this.current = current;
        this.discharge = discharge;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getDischarge() {
        return discharge;
    }

    public void setDischarge(double discharge) {
        this.discharge = discharge;
    }

    @Override
    public String toString() {
        return "BatteryState{" +
                "voltage=" + voltage +
                ", remain=" + remain +
                ", current=" + current +
                ", discharge=" + discharge +
                '}';
    }
}
