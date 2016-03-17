package ch.projecthelin.droneonboardapp.di;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules={TestAppModule.class})
public interface TestAppComponent {
}