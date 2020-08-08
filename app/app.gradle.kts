@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

apply("$rootDir/buildsystem/signingRelease.gradle")
android {
    defaultConfig {
        applicationId = "org.illegaller.ratabb.hishoot2i"
        vectorDrawables.useSupportLibrary = true

        multiDexEnabled = true // TODO: Desugar
        //
        val fileAuthority = "$applicationId.fileAuthority"
        val imageReceiverKey = "$applicationId.IMAGE_RECEIVER"
        buildConfigField("String", "FILE_AUTHORITY", "\"${fileAuthority}\"")
        buildConfigField("String", "IMAGE_RECEIVER", "\"${imageReceiverKey}\"")
        manifestPlaceholders = mapOf(
            "image_receiver_key" to imageReceiverKey,
            "file_authority" to fileAuthority
        )
        dependenciesInfo {
            // Disables dependency metadata when building APKs.
            includeInApk = false
            // Disables dependency metadata when building Android App Bundles.
            includeInBundle = false
        }
    }

    packagingOptions {
        exclude("kotlin/**")
        exclude("**/*.kotlin_metadata")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.version")
        exclude("META-INF/*.properties")
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
        getByName("release") {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            isZipAlignEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures.viewBinding = true
    compileOptions {
        coreLibraryDesugaringEnabled = true // TODO: Desugar
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
kapt {
    arguments {
        arg("dagger.experimentalDaggerErrorMessages", "enabled") //
        arg("dagger.formatGeneratedSource", "disabled")
    }
}
dependencies {

    implementation(project(":common"))
    implementation(project(":imageloader"))
    implementation(project(":template"))
    implementation(project(":entity"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10") // TODO: Desugar

    val kotlinVersion: String by rootProject
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    val xAnnotationVersion: String by rootProject
    compileOnly("androidx.annotation:annotation:$xAnnotationVersion")

    val xCoreVersion: String by rootProject
    implementation("androidx.core:core:$xCoreVersion")

    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.appcompat:appcompat-resources:1.2.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.activity:activity:1.1.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("androidx.fragment:fragment:1.2.5")
    implementation("androidx.recyclerview:recyclerview:1.1.0")

    implementation("com.google.android.material:material:1.2.0")

    val xLifeCycleVersion: String by rootProject
    implementation("androidx.lifecycle:lifecycle-viewmodel:$xLifeCycleVersion")

    // DI
    implementation("javax.inject:javax.inject:1")
    implementation("com.google.dagger:dagger:2.28.1") //

    val daggerHiltVersion: String by rootProject
    implementation("com.google.dagger:hilt-android:$daggerHiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$daggerHiltVersion")

    val xHiltVersion: String by rootProject
    kapt("androidx.hilt:hilt-compiler:$xHiltVersion")

    val timberVersion: String by rootProject
    implementation("com.jakewharton.timber:timber:$timberVersion")

    implementation("com.chibatching.kotpref:kotpref:2.11.0")

    implementation("com.squareup.leakcanary:plumber-android:2.4") //
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.4")

    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.0")
    implementation("io.reactivex.rxjava2:rxkotlin:2.2.0")
    implementation("org.reactivestreams:reactive-streams:1.0.2")
}

apply("$rootDir/buildsystem/appVersioning.gradle")
