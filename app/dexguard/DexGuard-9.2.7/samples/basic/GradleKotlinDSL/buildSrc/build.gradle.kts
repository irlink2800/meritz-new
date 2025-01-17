plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("../../../../lib")
    }
    mavenCentral()
    google()
}

dependencies {
        implementation("com.android.tools.build:gradle:7.0.0")
        //noinspection GradleDynamicVersion
        implementation("com.guardsquare:dexguard-gradle-plugin:+")
}

