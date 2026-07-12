@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SuperApp"

// ---- App ----
include(":app")

// ---- Core modules ----
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:domain")
include(":core:notifications")
include(":core:sync")
include(":core:testing")

// ---- Feature modules ----
include(":feature:onboarding")
include(":feature:auth")
include(":feature:home")
include(":feature:spaces")
include(":feature:editor")
include(":feature:create")
include(":feature:projects")
include(":feature:messages")
include(":feature:search")
include(":feature:notifications")
include(":feature:profile")
include(":feature:settings")
include(":feature:canvas")

// ---- Benchmark ----
include(":benchmark")
