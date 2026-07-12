plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.hilt)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.notifications"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(libs.androidx.core.ktx)
}
