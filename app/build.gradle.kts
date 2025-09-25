plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "umg.edu.gt.trainupapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "umg.edu.gt.trainupapp"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // Room for local database persistence
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Gson para TypeConverters de listas (IDs de músculos, equipamiento, etc.)
    implementation(libs.gson)

    // Retrofit + Gson converter para consumo de API wger
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson) // alias corregido
    // OkHttp Logging Interceptor (solo útil en debug, se condiciona en el builder)
    implementation(libs.okhttp.logging) // alias corregido

    // Glide para carga de imágenes
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}