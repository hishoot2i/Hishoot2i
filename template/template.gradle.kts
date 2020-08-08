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
