package ch.projecthelin.droneonboardapp.di;

import android.app.Application;
import android.content.Context;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public class TestAppModule {

    private ControlTower controlTower;
    private Drone drone;

    public TestAppModule() {
    }

    @Provides
    ControlTower providesControlTower(Context context) {
        return controlTower;
    }

    @Provides
    Drone providesDrone(Context context) {
        return drone;
    }


    public void setControlTower(ControlTower controlTower) {
        this.controlTower = controlTower;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }
}