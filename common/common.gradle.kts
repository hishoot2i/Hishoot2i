@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    kotlin("android")
}
dependencies {
    compileOnly(project(":entity"))

    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")

    val kotlinVersion: String by rootProject
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    val xCoreVersion: String by rootProject
    implementation("androidx.core:core:$xCoreVersion")
    implementation("androidx.appcompat:appcompat-resources:1.2.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    api("androidx.recyclerview:recyclerview:1.1.0")

    val timberVersion: String by rootProject
    implementation("com.jakewharton.timber:timber:$timberVersion")
}
