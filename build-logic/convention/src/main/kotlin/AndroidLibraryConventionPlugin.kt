import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                // No targetSdk here: LibraryDefaultConfig doesn't declare one — targetSdk is an
                // install-time behavior flag that only makes sense for an installable APK, not an
                // AAR. Only the app module's defaultConfig sets it (AndroidApplicationConventionPlugin.kt).

                testOptions {
                    unitTests.isIncludeAndroidResources = true
                }
            }
        }
    }
}
