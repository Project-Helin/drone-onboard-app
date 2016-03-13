package ch.projecthelin.droneonboardapp.dto.dronestate;

public class GPSState {

    private String fixType;
    private int sattelitesCount;
    private double posLat;
    private double posLon;

    public GPSState(){
        this.fixType = "no Signal";
        this.sattelitesCount = 0;
        this.posLat = 0;
        this.posLon = 0;
    }

    public GPSState(String fixType, int sattelitesCount, double posLat, double posLon){
        this.fixType = fixType;
        this.sattelitesCount = sattelitesCount;
        this.posLat = posLat;
        this.posLon = posLon;
    }

    public String getFixType() {
        return fixType;
    }

    public int getSattelitesCount() {
        return sattelitesCount;
    }

    public String getLatLong() {
        return posLat + " " + posLon;
    }

    public void setFixType(String fixType) {
        this.fixType = fixType;
    }

    public void setSattelitesCount(int sattelitesCount) {
        this.sattelitesCount = sattelitesCount;
    }

    @Override
    public String toString() {
        return "GPSState{" +
                "fixType='" + fixType + '\'' +
                ", sattelitesCount=" + sattelitesCount +
                ", posLat=" + posLat +
                ", posLon=" + posLon +
                '}';
    }
}
