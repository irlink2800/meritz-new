buildscript {
    repositories {
        maven {
            url = uri("../../../lib")
        }
        google()                        // For the Android plugin.
        mavenCentral()                       // For anything else.
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        //noinspection GradleDynamicVersion
        classpath("com.guardsquare:dexguard-gradle-plugin:+")
    }
}

