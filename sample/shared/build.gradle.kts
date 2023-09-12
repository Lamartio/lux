plugins {
    kotlin("multiplatform")
    id("com.android.library")
}
//
//repositories {
//    mavenLocal()
//    google()
//    mavenCentral()
//}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.lamart:lux:0.5.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

            }
        }
    }
}

android {
    namespace = "io.lamart.lux.sample"
    compileSdk = 33
    defaultConfig {
        minSdk = 27
    }
}