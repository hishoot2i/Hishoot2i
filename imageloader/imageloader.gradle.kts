@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    api(project(":common"))
    val kotlinVersion: String by rootProject
    api(kotlin("stdlib", version = kotlinVersion))
    val xCoreVersion: String by rootProject
    implementation("androidx.core:core-ktx:$xCoreVersion")
    val xLifeCycleVersion: String by rootProject
    api("androidx.lifecycle:lifecycle-common:$xLifeCycleVersion")
    val coilVersion: String by rootProject
    api("io.coil-kt:coil-base:$coilVersion")
    val okHttpVersion: String by rootProject
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
}
