// apply from: '../buildsystem/library_module.gradle'
//
// dependencies {
//    //
//    compileOnly deps.kotlin.stdlib
//    compileOnly deps.supportLibrary.annotations
//    compileOnly deps.inject
//    compileOnly project(':common')
//    compileOnly project(':entity')
//    compileOnly deps.universalImageLoader
// }
@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    kotlin("android")
}

dependencies {
    implementation(project(":common"))
    api(project(":entity"))

    val kotlinVersion: String by rootProject
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    api("com.nostra13.universalimageloader:universal-image-loader:1.9.5")
}
