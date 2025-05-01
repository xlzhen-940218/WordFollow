plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.xlzhen.wordfollow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xlzhen.wordfollow"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.0.2"

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2
    implementation(libs.fastjson2)
    implementation(project(":edgetts"))
}