plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cyphermoon.tictaczone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cyphermoon.tictaczone"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // version declarations
    val lifecycle_version = "2.7.0"
    val nav_version = "2.7.7"

    // Import Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.androidx.lifecycle.runtime.compose)
    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    //Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    //redux
    implementation("org.reduxkotlin:redux-kotlin-threadsafe-android:0.6.1")
    //redux-thunk
    implementation("org.reduxkotlin:redux-kotlin-thunk-android:0.6.0")
    //redux-kotlin-compose
    implementation("org.reduxkotlin:redux-kotlin-thunk-android:0.6.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    //Native dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}