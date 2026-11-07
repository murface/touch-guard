plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.touchguard.peloton"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.touchguard.peloton"
        minSdk = 30
        targetSdk = 30 // Dedicated, sideloaded Android 11 tablet proof of concept.
        versionCode = 3
        versionName = "0.1.2"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    val releaseKey = System.getenv("TOUCHGUARD_KEYSTORE")
    if (!releaseKey.isNullOrBlank()) {
        signingConfigs.create("distribution") {
            storeFile = file(releaseKey)
            storePassword = System.getenv("TOUCHGUARD_STORE_PASSWORD")
            keyAlias = System.getenv("TOUCHGUARD_KEY_ALIAS") ?: "touchguard"
            keyPassword = System.getenv("TOUCHGUARD_KEY_PASSWORD")
        }
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("distribution")
    }
}
dependencies { testImplementation("junit:junit:4.13.2") }
