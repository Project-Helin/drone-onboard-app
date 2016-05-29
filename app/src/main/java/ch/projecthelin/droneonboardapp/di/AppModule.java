package ch.projecthelin.droneonboardapp.di;

import android.app.Application;
import android.content.Context;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
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
        return new ControlTower(context);
    }

    @Provides
    @Singleton
    Drone providesDrone(Context context) {
        return new Drone(context);
    }
}