plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.petpawcalendar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.petpawcalendar"
        minSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI extra
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Lottie (animaciones .json)
    implementation(libs.lottie)

    // Networking: Retrofit + Gson + OkHttp logging
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Imágenes (avatares/fotos)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Calendar mensual
    //implementation(libs.material.calendarview)

}