import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention applied to every `:feature:*` module: Android library + Compose + Hilt,
 * plus the `core` modules every feature is allowed to depend on.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("superapp.android.library")
            pluginManager.apply("superapp.android.library.compose")
            pluginManager.apply("superapp.android.hilt")
            pluginManager.apply("superapp.android.testing")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:domain"))
                // Preferences are simple, reactive settings rather than complex
                // business logic, so features read them directly instead of
                // through a core:domain use-case indirection. See docs/DECISIONS.md.
                add("implementation", project(":core:datastore"))

                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
                // Screens since Phase 1 (feature:create's StickyNote2/Draw/Groups, feature:home/
                // spaces' AutoMirrored Article/Message, Phase 4's block-type icons) use icons
                // outside the small curated `material-icons-core` set. core:designsystem depends
                // on the extended set for its own components but only as `implementation`, so it
                // doesn't leak here transitively — every feature needs its own icon access anyway.
                add("implementation", libs.findLibrary("androidx-compose-material-icons-extended").get())
            }
        }
    }
}
