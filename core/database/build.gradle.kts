plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.room)
    alias(libs.plugins.superapp.android.hilt)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.database"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.runtime)

    // ApplicationProvider for the Robolectric-backed in-memory Room DAO tests.
    testImplementation(libs.androidx.test.ext.junit)
}
