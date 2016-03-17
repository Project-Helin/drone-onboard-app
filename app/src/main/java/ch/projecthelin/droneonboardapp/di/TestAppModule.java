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

    Application application;

    public TestAppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    ControlTower providesControlTower(Context context) {
        return mock(ControlTower.class);
    }

    @Provides
    @Singleton
    Drone providesDrone(Context context) {
        return mock(Drone.class);
    }


}