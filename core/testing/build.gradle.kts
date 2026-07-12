plugins {
    alias(libs.plugins.superapp.android.library)
}

android {
    namespace = "com.adrien.superapp.core.testing"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:domain"))
    api(libs.junit4)
    api(libs.truth)
    api(libs.turbine)
    api(libs.mockk)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.espresso.core)
}
