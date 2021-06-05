plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId("com.pedro.rtpstream")
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = Constants.VERSION_CODE
        versionName = Constants.VERSION_NAME
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":rtplibrary"))
    implementation("com.google.android.material:material:1.3.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")
}
