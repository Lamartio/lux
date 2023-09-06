plugins {
    kotlin("multiplatform")
//    id("maven-publish")
    id("convention.publication")
}

group = "io.lamart"
version = "0.5.0"

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

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
                implementation(platform("io.arrow-kt:arrow-stack:1.2.0"))
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