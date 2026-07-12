plugins {
    alias(libs.plugins.superapp.android.library)
    alias(libs.plugins.superapp.android.hilt)
    alias(libs.plugins.superapp.android.testing)
}

android {
    namespace = "com.adrien.superapp.core.domain"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(libs.androidx.paging.runtime)
    implementation(libs.kotlinx.coroutines.core)

    // Not a cycle: core:testing's main sourceSet depends on core:domain's main
    // sourceSet, and this is core:domain's *test* configuration depending on
    // core:testing — different configurations, so Gradle builds core:domain's
    // main sources first, then core:testing, then core:domain's tests.
    testImplementation(project(":core:testing"))
}
