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
    val xCoreVersion: String by rootProject
    implementation("androidx.core:core:$xCoreVersion")
    val xDocumentFileVersion: String by rootProject
    implementation("androidx.documentfile:documentfile:$xDocumentFileVersion")
    val timberVersion: String by rootProject
    implementation("com.jakewharton.timber:timber:$timberVersion")
}
