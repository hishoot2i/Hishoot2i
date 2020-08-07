@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
}
dependencies {
    val kotlinVersion: String by rootProject
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}
