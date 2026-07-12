package com.adrien.superapp.core.common.di

import javax.inject.Qualifier

enum class SuperAppDispatcher {
    Default,
    IO,
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val superAppDispatcher: SuperAppDispatcher)
