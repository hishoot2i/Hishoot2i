@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
}
android {
    lintOptions.ignore("IconMissingDensityFolder") // frame1.9.png @ mdpi only
    buildTypes.getByName("release").consumerProguardFile("proguard-template.pro")
}
dependencies {
    val kotlinVersion: String by rootProject
    api(kotlin("stdlib", version = kotlinVersion))
    api(project(":common"))
    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")
    val xCoreVersion: String by rootProject
    implementation("androidx.core:core-ktx:$xCoreVersion")

    val serializationVersion: String by rootProject
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$serializationVersion")
    val xmlUtilVersion: String by rootProject
    implementation("net.devrieze:xmlutil-android:$xmlUtilVersion")
    api("net.devrieze:xmlutil-serialization-android:$xmlUtilVersion")
}
