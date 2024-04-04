package com.example.avitotechweathertraineetask.data.hilt

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Singleton
//    @Provides
//    fun provideContext(application: WeatherApplication): Context = application.applicationContext

//    @Singleton
//    @Provides
//    fun provideApplication(@ApplicationContext app: Context): WeatherApplication{
//        return app as WeatherApplication
//    }
}