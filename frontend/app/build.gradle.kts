import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// local.properties 불러오기
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.buulgyeonE202.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.buulgyeonE202.frontend"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 2. 🔥 [추가] S3_BUCKET_NAME 설정
        // local.properties에서 읽어오고, 없으면 빈 문자열("") 또는 기본값 사용
        val s3BucketName = localProperties.getProperty("S3_BUCKET_NAME") ?: ""

        // BuildConfig에 String 타입으로 심어주기 (따옴표 이스케이프 \" 주의)
        buildConfigField("String", "S3_BUCKET_NAME", "\"$s3BucketName\"")
        // [AWS 키 설정] 따옴표(\") 처리 및 null 방지
        val awsAccessKey = localProperties.getProperty("AWS_ACCESS_KEY_ID") ?: ""
        val awsSecretKey = localProperties.getProperty("AWS_SECRET_ACCESS_KEY") ?: ""

        buildConfigField("String", "AWS_ACCESS_KEY_ID", "\"$awsAccessKey\"")
        buildConfigField("String", "AWS_SECRET_ACCESS_KEY", "\"$awsSecretKey\"")

        // local.properties에서 읽어오고, 없으면 빈 값("") 처리
        val baseUrl = localProperties.getProperty("BASE_URL") ?: ""

        // BuildConfig.BASE_URL 변수로 생성 (따옴표 이스케이프 주의)
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
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

    // 자바 버전 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 코틀린 JVM 타겟 17
    kotlinOptions {
        jvmTarget = "17"
    }


    // MediaPipe 라이브러리가 중복될 경우를 대비한 설정
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//            pickFirsts += "lib/x86_64/libmediapipe_tasks_vision_jni.so"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    // 1. 기초 라이브러리 (버전 관리 통합)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // 2. Compose BOM 설정 (안정적인 2024.02.01 버전 권장)
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    // 프리뷰
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material3:material3")

    // 🔥 [핵심 수정] reorderable 라이브러리와 호환되는 Foundation 버전 강제 지정
    // 1.7.0 이상의 최신 버전에서는 메서드 이름이 바뀌어 에러가 납니다. 1.6.x 대를 사용하세요.
    implementation("androidx.compose.foundation:foundation:1.6.8")

    // ... (기존 테스트 관련 코드 생략) ...

    // 3. Navigation (버전 통일)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 4. Retrofit & Hilt
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // 5. 드래그 앤 드롭 라이브러리
    // 현재 버전 0.9.6을 유지하되 위에서 foundation 버전을 맞춰줬으므로 에러가 사라집니다.
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    // 5. CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // ✅ 추가: VideoCapture(Recorder)
    implementation("androidx.camera:camera-video:${cameraxVersion}")

    // 6. MediaPipe
    implementation("com.google.mediapipe:tasks-vision:0.10.14")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.amazonaws:aws-android-sdk-s3:2.73.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // 7. Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // 8. 통신 로그
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 9. 자동로그인 구현
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}