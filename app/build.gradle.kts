plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.kotlin.parecelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mmfsin.betweenminds"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.mmfsin.betweenminds"
        minSdk = 26
        targetSdk = 36
        versionCode = 23
        versionName = "1.2.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

    /** ViewModel */
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    /** Constraint Layout */
    implementation(libs.androidx.constraintlayout.compose)

    /** Coil -> Imágenes */
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    /** Navigation */
    implementation(libs.androidx.navigation.compose)

    /** Serialization */
    implementation(libs.kotlin.serialization.json)

    /** Hilt */
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)

    /** Firebase */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)

    /** Google SingIn */
    implementation(libs.play.services.auth)

    /** Room DDBB */
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    /** DataStore */
    implementation(libs.androidx.datastore.preferences)

    /** Lottie */
    implementation(libs.lottie.compose)

    /** Billing */
    implementation(libs.billing.ktx)

    /** Reoderable Recyclers */
    implementation("sh.calvin.reorderable:reorderable:2.4.0")

    //Animate BG
    implementation("nl.dionsegijn:konfetti-compose:2.0.5")
    implementation("com.github.doctoror.particlesdrawable:library:2.0.2")

    /** Testing */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}