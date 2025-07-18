plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.battlecatsreinforcementlearning"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.battlecatsreinforcementlearning"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add native library support for TensorFlow Lite
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
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

    // Add packaging options for native libraries
    packagingOptions {
        pickFirst("**/libc++_shared.so")
        pickFirst("**/libjsc.so")
    }

    // Add aaptOptions for large models
    aaptOptions {
        noCompress("tflite")
        noCompress("lite")
        noCompress("bin")
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // TensorFlow Lite for machine learning
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")

    // Additional ML and math libraries
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.metadata)

    // OpenCV for image processing (useful for game state analysis)
    implementation(libs.opencv.android)

    // JSON processing for model configuration and data serialization
    implementation("com.google.code.gson:gson:2.13.1")

    // Network libraries for potential model updates/logging
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Room database for storing training data and experiences
    implementation("androidx.room:room-runtime:2.7.2")
    annotationProcessor("androidx.room:room-compiler:2.7.2")

    // WorkManager for background training tasks
    implementation("androidx.work:work-runtime:2.10.2")

    // Coroutines for asynchronous operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.9.2")

    // For mathematical operations in RL algorithms
    implementation(libs.commons.math3)

    // For efficient array operations
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.robolectric:robolectric:4.15.1")

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
}