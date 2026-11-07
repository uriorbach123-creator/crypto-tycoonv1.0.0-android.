plugins {
    id("com.android.application")
}

android {
    namespace = "com.cryptotycoon.game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cryptotycoon.game"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.6.0")
}
