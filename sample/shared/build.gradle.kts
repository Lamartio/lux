plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

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
//        it.binaries.framework {
//            baseName = "shared"
//        }

        it.binaries {
            framework {
                baseName = "shared"
            }
            sharedLib {
                export(project(":lux"))
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":lux"))
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