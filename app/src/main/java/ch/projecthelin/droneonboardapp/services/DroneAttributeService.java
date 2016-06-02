package ch.projecthelin.droneonboardapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.helin.messages.dto.message.DroneDto;
import ch.projecthelin.droneonboardapp.listeners.DroneAttributeUpdateReceiver;

@Singleton
public class DroneAttributeService {

    private List<DroneAttributeUpdateReceiver> attributesUpdateListenerList = new ArrayList<>();


    private DroneDto droneDto;

    @Inject
    public DroneAttributeService(Context context){
        DroneDto droneDto = new DroneDto();
        this.droneDto = new DroneDto();

    }

    public void updateDroneAttributes(Context context, DroneDto droneDto) {
        this.droneDto = droneDto;




    }


    public void notifyListener(){
        for(DroneAttributeUpdateReceiver droneAttributesUpdateListener : attributesUpdateListenerList){
           // droneAttributesUpdateListener.onDroneAttributeUpdate(droneDto);
        }
    }

    public DroneDto getDroneDto() {
        return droneDto;
    }

    public void setDroneDto(DroneDto droneDto) {
        this.droneDto = droneDto;
    }


}
