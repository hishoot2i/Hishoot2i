@file:Suppress("SpellCheckingInspection")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val agpVersion: String by project
    val daggerHiltVersion: String by project
    val kotlinVersion: String by project
    val xNavigationVersion: String by project
    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/pdvrieze/maven")
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$daggerHiltVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$xNavigationVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/pdvrieze/maven")
        gradlePluginPortal()
    }
    afterEvaluate {
        tasks.withType(KotlinCompile::class) {
            kotlinOptions {
                jvmTarget = "1.8"
                allWarningsAsErrors = true
                verbose = true
                useIR = true
            }
        }
    }
    apply("$rootDir/buildsystem/spotless.gradle")
    // NOTE: avoid duplication libs with different version!
    configurations.all {
        resolutionStrategy.eachDependency {
            val coroutinesVersion: String by project
            val kotlinVersion: String by project
            val okHttpVersion: String by project
            val xArchCoreVersion: String by project
            val xCollectionVersion: String by project
            val xLifeCycleVersion: String by project
            when (requested.group) {
                "androidx.arch.core" -> useVersion(xArchCoreVersion)
                "androidx.collection" -> useVersion(xCollectionVersion)
                "androidx.lifecycle" -> useVersion(xLifeCycleVersion)
                "com.squareup.okhttp3" -> useVersion(okHttpVersion)
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlinx" -> {
                    if (requested.name.startsWith("kotlinx-coroutines")) {
                        useVersion(coroutinesVersion)
                    }
                }
            }
        }
    }
}
plugins {
    id("com.diffplug.spotless") version "5.11.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    id("com.autonomousapps.dependency-analysis") version "0.71.0"
}
/** Plugin [com.autonomousapps.dependency-analysis] config. */
dependencyAnalysis {
    issues { all { onAny { severity("fail") } } }
}
/** Plugin `dependencyUpdates` [com.github.ben-manes.versions] config. */
tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java) {
    fun isStable(version: String) = Regex("^[0-9,.v-]+(-r)?$").matches(version)
    rejectVersionIf { !isStable(candidate.version) && isStable(currentVersion) }
    checkForGradleUpdate = false //
}
subprojects {
    afterEvaluate {
        val isAndroidLib = plugins.hasPlugin("com.android.library")
        val isAndroidApp = plugins.hasPlugin("com.android.application")
        if (isAndroidLib || isAndroidApp) apply("$rootDir/buildsystem/androidCommon.gradle")
    }
}
