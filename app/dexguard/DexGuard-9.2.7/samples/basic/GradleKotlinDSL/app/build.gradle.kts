plugins {
    id("com.android.application")
    id("dexguard")
}

repositories {
    google()  // For the Android support libraries.
    mavenCentral() // For any other libraries.
}

android {
    compileSdkVersion(28)

    signingConfigs {
        getByName("debug") {
            storeFile     = file("../../../debug.keystore")
            storePassword = "android"
            keyAlias      = "androiddebugkey"
            keyPassword   = "android"
        }
    }

    defaultConfig {
        minSdkVersion(4)
        targetSdkVersion(29)
        signingConfig = signingConfigs["debug"]
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDir("src")
            resources.srcDir("src")
            aidl.srcDir("src")
            renderscript.srcDir("src")
            res.srcDir("res")
            assets.srcDir("assets")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

dexguard {
    path = "../../../.."
    configurations {
        register("debug") {
            defaultConfiguration("dexguard-debug.pro")
            configuration("dexguard-project.txt")
        }
        register("release") {
            defaultConfiguration("dexguard-release-aggressive.pro")
            configuration("dexguard-project.txt")
        }
    }
}

