package ch.projecthelin.droneonboardapp.di;

import android.content.Context;
import ch.projecthelin.droneonboardapp.activities.MainActivity;
import ch.projecthelin.droneonboardapp.activities.RegisterDroneActivity;
import ch.projecthelin.droneonboardapp.fragments.OverviewFragment;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules={TestAppModule.class})
public interface TestAppComponent {
   void inject(MainActivity activity);
   void inject(RegisterDroneActivity activity);
   void inject(OverviewFragment fragment);
   Context context();
}