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

    val coilVersion: String by rootProject
    implementation("io.coil-kt:coil-base:$coilVersion")

    val okHttpVersion: String by rootProject
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    val coroutinesVersion: String by rootProject
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutinesVersion")
}
