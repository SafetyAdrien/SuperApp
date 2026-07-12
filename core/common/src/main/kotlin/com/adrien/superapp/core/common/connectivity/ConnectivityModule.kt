package com.adrien.superapp.core.common.connectivity

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindsConnectivityObserver(
        impl: ConnectivityObserverImpl,
    ): ConnectivityObserver
}
