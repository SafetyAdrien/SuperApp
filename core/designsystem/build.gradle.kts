plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.library.compose)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material.icons.extended)
}
