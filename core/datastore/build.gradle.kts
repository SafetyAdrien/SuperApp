plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.hilt)
    alias(libs.plugins.superapp.android.testing)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.adrien.superapp.core.datastore"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)
}
