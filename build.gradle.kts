@file:Suppress("SpellCheckingInspection")

buildscript {
    val agpVersion: String by project
    val daggerHiltVersion: String by project
    val kotlinVersion: String by project
    val xNavigationVersion: String by project

    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$daggerHiltVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$xNavigationVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
    }
    afterEvaluate {
        tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
            kotlinOptions {
                jvmTarget = "1.8"
                allWarningsAsErrors = true
                verbose = true
            }
        }
    }
}
plugins {
    id("com.diffplug.spotless") version "5.1.1"
    id("com.github.ben-manes.versions") version "0.29.0"
    id("com.autonomousapps.dependency-analysis") version "0.56.0"
}
/** Plugin [com.autonomousapps.dependency-analysis] config. */
dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail") // <'fail'|'warn'|'ignore'> default is 'warn'
                exclude("androidx.multidex:multidex") // TODO: Using Desugar?
            }
            // or configure per-type
            /*onUnusedDependencies { ... }
            onUsedTransitiveDependencies { ... }
            onIncorrectConfiguration { ... }
            onRedundantPlugins { ... } // no excludes in this case*/
        }
    }
}
/** Plugin [com.github.ben-manes.versions] config. */
tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
            version.toUpperCase().contains(it)
        }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }
    rejectVersionIf { isNonStable(candidate.version) }
    checkForGradleUpdate = false //
}

subprojects {
    apply("$rootDir/buildsystem/spotless.gradle")
    afterEvaluate {
        val isAndroidLib = plugins.hasPlugin("com.android.library")
        val isAndroidApp = plugins.hasPlugin("com.android.application")
        if (isAndroidLib || isAndroidApp) apply("$rootDir/buildsystem/androidCommon.gradle")
    }
}
