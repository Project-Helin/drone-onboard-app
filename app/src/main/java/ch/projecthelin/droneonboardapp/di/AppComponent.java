package ch.projecthelin.droneonboardapp.di;

import android.content.Context;
import ch.projecthelin.droneonboardapp.activities.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {
   void inject(MainActivity activity);
   Context context();
}