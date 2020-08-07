// apply from: '../buildsystem/library_module.gradle'
//
// dependencies {
//    //
//    compileOnly deps.kotlin.stdlib
//    compileOnly deps.supportLibrary.annotations
//    compileOnly deps.inject
//    compileOnly project(':common')
//    compileOnly project(':entity')
// }
plugins {
    id("com.android.library")
    kotlin("android")
}

dependencies {
    api(project(":common"))
    api(project(":entity"))

    val kotlinVersion: String by rootProject
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")
}
