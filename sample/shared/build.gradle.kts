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

    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries {
//            framework {
//                baseName = "shared"
//            }
//            sharedLib {
//                export(project(":lux"))
//            }
//        }
//    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":lux"))
//                implementation("io.lamart:lux:0.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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