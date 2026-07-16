import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Shared Android + Kotlin configuration used by both `com.android.application`
 * and `com.android.library` convention plugins.
 *
 * AGP 9.0 removed `CommonExtension`'s six generic type parameters and moved its
 * `defaultConfig{}`/`compileOptions{}`/`lint{}` trailing-lambda functions onto the concrete
 * `ApplicationExtension`/`LibraryExtension` types, leaving only property getters on the shared
 * `CommonExtension` interface this function takes — hence `.apply {}` on each nested object
 * instead of the old block-call syntax. See docs/DECISIONS.md.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = 37

        defaultConfig.apply {
            minSdk = 31
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }

        lint.apply {
            abortOnError = true
            warningsAsErrors = false
            checkDependencies = true
        }
    }

    pluginManager.apply("org.jetbrains.kotlin.android")

    extensions.configure(KotlinAndroidProjectExtension::class.java) {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
        }
    }

    dependencies {
        add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
    }
}
