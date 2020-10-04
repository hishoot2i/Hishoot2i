plugins {
    id("com.android.library")
    kotlin("android")
}
android.lintOptions.ignore("IconMissingDensityFolder") // frame1.9.png @ mdpi only
dependencies {
    val kotlinVersion: String by rootProject
    api(kotlin("stdlib", version = kotlinVersion))
    api(project(":common"))
    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")
}
