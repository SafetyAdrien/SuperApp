plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
