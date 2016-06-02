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

    private static final String DRONE_ACTIVE = "drone_active";
    private static final Boolean DRONE_ACTIVE_DEFAULT = true;
    private static final String DRONE_NAME = "drone_name";
    private static final String DRONE_NAME_DEFAULT = "John Drone";
    private static final String DRONE_PAYLOAD = "drone_payload";
    private static final int DRONE_PAYLOAD_DEFAULT = 0;

    private DroneDto droneDto;

    @Inject
    public DroneAttributeService(Context context){
        DroneDto droneDto = new DroneDto();
        this.droneDto = new DroneDto();

    }

    public void updateDroneAttributes(Context context, DroneDto droneDto) {
        this.droneDto = droneDto;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Boolean droneActiveState = droneDto.isActive();
        editor.putBoolean(DRONE_ACTIVE, droneActiveState);

        droneDto.getName();
        droneDto.getPayload();
        droneDto.isActive();


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
