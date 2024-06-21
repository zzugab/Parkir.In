plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {

    namespace = "com.sugab.parkirin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sugab.parkirin"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Firebase firestore
    implementation (libs.firebase.firestore.ktx) // Check for the latest version
    implementation(libs.firebase.bom)


    //ViewPager2
    implementation (libs.androidx.viewpager2)

    //Coroutine
    implementation (libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)


    //Fragment
    implementation (libs.androidx.fragment.ktx)

    //Recyclerview
    implementation (libs.androidx.recyclerview) // Update version if needed

    //Ml Kit
    implementation(libs.text.recognition)
}
// Apply the Google services Gradle plugin
apply(plugin = "com.google.gms.google-services")

