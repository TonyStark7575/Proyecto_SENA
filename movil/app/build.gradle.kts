// Módulo de la aplicación: versiones de Android, librerías y pruebas
plugins {
    id("com.android.application")
}

android {
    namespace = "com.loshilosdemaya.movil"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.loshilosdemaya.movil"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        // URL inicial del servidor: 10.0.2.2 es el "localhost" del computador visto desde el emulador.
        // Se puede cambiar en la pantalla de inicio de sesión sin recompilar
        buildConfigField("String", "URL_SERVIDOR_INICIAL", "\"http://10.0.2.2/ProyectoSENA/public/\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // ===== Capa de presentación (vista) =====
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.12.4")
    // Imágenes de productos descargadas del servidor
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ===== Patrón MVVM: ViewModel + LiveData =====
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.10.0")

    // ===== Capa de red: consumo de la API REST =====
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ===== Pruebas unitarias =====
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
