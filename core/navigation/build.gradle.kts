plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.library.compose)
    alias(libs.plugins.superapp.android.testing)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.adrien.superapp.core.navigation"
}

dependencies {
    api(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}
