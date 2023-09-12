plugins {
    kotlin("multiplatform")
//    id("maven-publish")
    id("convention.publication")
}

group = "io.lamart"
version = "0.5.2"

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "Lux"
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

// https://github.com/alllex/parsus/blob/main/build.gradle.kts
// Without this there is a Gradle error (notice mismatch between publish task and sign names):
// > Reason: Task ':publishIosArm64PublicationToMavenLocal' uses this output of task ':signIosX64Publication' without declaring an explicit or implicit dependency.
tasks.withType<AbstractPublishToMaven>().configureEach {
    mustRunAfter(tasks.withType<Sign>())
}