@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    kotlin("android")
}

dependencies {
    val kotlinVersion: String by rootProject
    api(kotlin("stdlib", version = kotlinVersion))
    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")
    api("io.reactivex.rxjava2:rxjava:2.2.19")
}
