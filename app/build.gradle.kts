plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")  // 确保这行没有错误
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.BookRecord"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.BookRecord"
        minSdk = 24
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
        kotlinCompilerExtensionVersion="1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation("com.google.firebase:firebase-database-ktx:20.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.3")

    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("com.google.dagger:hilt-android:2.38.1")
    ksp("com.google.dagger:hilt-android-compiler:2.38.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    // 如果使用 AndroidX 的 ViewModel
    //implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    //kapt ("androidx.hilt:hilt-compiler:1.0.0-alpha03")
    // Add the dependency for the Realtime Database library
    // When NOT using the BoM, you must specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    // Annotation processor
    kapt ("androidx.lifecycle:lifecycle-compiler:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
}


