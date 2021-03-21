@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
}
android.buildTypes.getByName("release").consumerProguardFile("proguard-common.pro")
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

    val serializationVersion: String by rootProject
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$serializationVersion")
}
