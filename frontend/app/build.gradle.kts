plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.buulgyeonE202.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.buulgyeonE202.frontend"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
<<<<<<< Updated upstream
=======

        // [AWS 키 설정] 따옴표(\") 처리 및 null 방지
        val awsAccessKey = localProperties.getProperty("AWS_ACCESS_KEY_ID") ?: ""
        val awsSecretKey = localProperties.getProperty("AWS_SECRET_ACCESS_KEY") ?: ""

        buildConfigField("String", "AWS_ACCESS_KEY_ID", "\"$awsAccessKey\"")
        buildConfigField("String", "AWS_SECRET_ACCESS_KEY", "\"$awsSecretKey\"")
        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("SERVER_URL")}\"")
        buildConfigField("String", "AWS_REGION", "\"${localProperties.getProperty("AWS_REGION")}\"")
        buildConfigField("String", "S3_BUCKET_NAME", "\"${localProperties.getProperty("S3_BUCKET_NAME")}\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
>>>>>>> Stashed changes
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 1. Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 2. Retrofit2 & Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 3. Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-android-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // 4. DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // 5. CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // 6. MediaPipe
    implementation("com.google.mediapipe:tasks-vision:0.10.14")

    // 7. 기타
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.code.gson:gson:2.10.1")
}