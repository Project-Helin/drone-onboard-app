package ch.projecthelin.droneonboardapp;

import android.app.Application;
import ch.projecthelin.droneonboardapp.di.AppComponent;
import ch.projecthelin.droneonboardapp.di.AppModule;
import ch.projecthelin.droneonboardapp.di.DaggerAppComponent;

public class DroneOnboardApp extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //Here we use the generated Dagger{{myComponentName}} class
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent component() {
        return appComponent;
    }

}