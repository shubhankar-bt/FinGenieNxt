plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
    id("kotlin-kapt") // Required for Hilt
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.shubhanya.fingenienxt"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shubhanya.fingenienxt"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // Use a version compatible with your Kotlin version
    }
    packagingOptions { // Add this if you encounter issues with duplicate files
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation(platform("androidx.compose:compose-bom:2024.05.00")) // Check for latest Compose BoM
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core") // For icons
    implementation("androidx.compose.material:material-icons-extended") // For more icons

    implementation("androidx.core:core-ktx:1.13.1") // Or latest
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0") // Or latest
    implementation("androidx.activity:activity-compose:1.9.0") // Or latest

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0")) // Or latest
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx") // Optional

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Or latest

    // ViewModel Lifecycle for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // Or latest

    // Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1") // Or latest
    kapt("com.google.dagger:hilt-compiler:2.51.1") // Or latest
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Hilt integration with Navigation Compose

    // Coil for image loading (if you plan to add images, e.g., for categories or profile pictures)
     implementation("io.coil-kt:coil-compose:2.6.0")

    // Testing (optional for now, but good practice)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")



}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

