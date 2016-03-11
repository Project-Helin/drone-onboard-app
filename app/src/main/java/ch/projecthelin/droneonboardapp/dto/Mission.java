package ch.projecthelin.droneonboardapp.dto;

import java.util.Date;

public class Mission {
    private String id;
    private MissionState missionState;
    private Date startTime;
    private Date endTime;

    public String getId() {
        return id;
    }

    public MissionState getMissionState() {
        return missionState;
    }

    public void setMissionState(MissionState missionState) {
        this.missionState = missionState;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
