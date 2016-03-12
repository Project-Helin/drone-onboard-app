package ch.projecthelin.droneonboardapp.services;

public class GPSState {

    private String fixType;
    private int sattelitesCount;
    private String latLong;

    public GPSState(){
        //default constructor
    }

    public GPSState(String fixType, int sattelitesCount, String latLong){
        this.fixType = fixType;
        this.sattelitesCount = sattelitesCount;
        this.latLong = latLong;
    }

    public String getFixType() {
        return fixType;
    }

    public int getSattelitesCount() {
        return sattelitesCount;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setFixType(String fixType) {
        this.fixType = fixType;
    }

    public void setSattelitesCount(int sattelitesCount) {
        this.sattelitesCount = sattelitesCount;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    @Override
    public String toString() {
        return "GPSState{" +
                "fixType='" + fixType + '\'' +
                ", sattelitesCount=" + sattelitesCount +
                ", latLong='" + latLong + '\'' +
                '}';
    }
}
