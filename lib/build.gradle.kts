import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "io.lamart"
version = "0.5.0"

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    android()
    ios {
        binaries {
            framework {
                baseName = "Lux"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(platform("io.arrow-kt:arrow-stack:1.2.0-RC"))
                api("io.arrow-kt:arrow-core")
                api("io.arrow-kt:arrow-optics")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
            }
        }
    }
}

android {
    namespace = "io.lamart.lux"
    compileSdk = 29
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}