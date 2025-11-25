plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.example.notespote"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.notespote"
        minSdk = 29
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Mis dependencias
    val composeBom = platform("androidx.compose:compose-bom:2025.10.01")
    implementation(composeBom)
    testImplementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

// Material Design 3
    implementation("androidx.compose.material3:material3")
// Iconos Extendidos (para iconos menos comunes)
    implementation("androidx.compose.material:material-icons-extended")

// Navegación moderna en Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel integrado con Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Lifecycle reactivo (collectAsStateWithLifecycle)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")


    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // El procesador de anotaciones (usa ksp en lugar de kapt/annotationProcessor)
    ksp("androidx.room:room-compiler:$roomVersion")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // Herramientas de depuración (solo en debug)
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Manifiesto para pruebas (necesario para UI tests)
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.5")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)

    // Apache POI (para leer/escribir archivos Office: Word, Excel, PowerPoint)
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)

}