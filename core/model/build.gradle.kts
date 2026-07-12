plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.testing)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.adrien.superapp.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
