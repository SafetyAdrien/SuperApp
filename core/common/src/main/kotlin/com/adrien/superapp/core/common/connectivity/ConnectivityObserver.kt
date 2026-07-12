package com.adrien.superapp.core.common.connectivity

import kotlinx.coroutines.flow.StateFlow

/** Observes whether the device currently has a usable network connection. */
interface ConnectivityObserver {
    val isOnline: StateFlow<Boolean>
}
