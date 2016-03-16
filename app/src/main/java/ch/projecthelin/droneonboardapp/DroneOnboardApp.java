package ch.projecthelin.droneonboardapp;

import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.di.AppModule;
import ch.projecthelin.droneonboardapp.di.AppComponent;
import ch.projecthelin.droneonboardapp.di.DaggerAppComponent;

public class DroneOnboardApp extends android.app.Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Dagger%COMPONENT_NAME%
        appComponent = DaggerAppComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .build();
    }

    public static void inject(MainActivity target) {
        appComponent.inject(target);
    }

}