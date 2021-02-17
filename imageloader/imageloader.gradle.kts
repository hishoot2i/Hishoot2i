@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    api(project(":common"))
    api("com.nostra13.universalimageloader:universal-image-loader:1.9.5")
    val kotlinVersion: String by rootProject
    api(kotlin("stdlib", version = kotlinVersion))
}
