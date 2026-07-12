plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.room)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
}
