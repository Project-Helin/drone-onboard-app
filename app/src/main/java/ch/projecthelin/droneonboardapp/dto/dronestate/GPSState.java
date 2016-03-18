package ch.projecthelin.droneonboardapp.dto.dronestate;

import com.o3dr.services.android.lib.coordinate.LatLong;

public class GPSState {

    private int fixType;
    private int satellitesCount;
    private double posLat;
    private double posLon;

    public GPSState() {
        this.satellitesCount = 0;
        this.posLat = 0;
        this.posLon = 0;
    }

    public boolean isGPSGood() {
        //if smaller than 2, GPS is not fixed not 2D, nor 3D
        return !(getFixType() < 2);
    }

    public int getFixType() {
        return fixType;
    }

    public String getFixTypeLabel() {
        return fixType < 2 ? "no connection" : this.fixType + "D";
    }

    public int getSatellitesCount() {
        return satellitesCount;
    }

    public String getLatLong() {
        return posLat + " " + posLon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPSState gpsState = (GPSState) o;

        if (fixType != gpsState.fixType) return false;
        if (satellitesCount != gpsState.satellitesCount) return false;
        if (Double.compare(gpsState.posLat, posLat) != 0) return false;
        return Double.compare(gpsState.posLon, posLon) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = fixType;
        result = 31 * result + satellitesCount;
        temp = Double.doubleToLongBits(posLat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(posLon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setPosition(LatLong position) {
        if (position != null) {
            this.posLat = position.getLatitude();
            this.posLon = position.getLongitude();
        }
    }

    public void setFixType(int fixType) {
        this.fixType = fixType;
    }

    public void setSatellitesCount(int satellitesCount) {
        this.satellitesCount = satellitesCount;
    }

    @Override
    public String toString() {
        return "GPSState{" +
                "fixType='" + getFixTypeLabel() + '\'' +
                ", satellitesCount=" + satellitesCount +
                ", posLat=" + posLat +
                ", posLon=" + posLon +
                '}';
    }
}
