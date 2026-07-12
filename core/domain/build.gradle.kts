plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.hilt)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
}
