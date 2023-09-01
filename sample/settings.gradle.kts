pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sample"
include(":androidApp")
include(":shared")
include(":lib")
project(":lib").projectDir = file("${rootDir.parent}/lib")
