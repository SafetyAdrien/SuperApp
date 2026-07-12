package com.adrien.superapp

import android.app.Application
import com.adrien.superapp.core.common.di.ApplicationScope
import com.adrien.superapp.core.database.seed.DemoDataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SuperAppApplication : Application() {

    @Inject
    lateinit var demoDataSeeder: DemoDataSeeder

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { demoDataSeeder.seedIfEmpty() }
    }
}
