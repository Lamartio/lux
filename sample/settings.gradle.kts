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
include(":lux")
project(":lux").projectDir = file("${rootDir.parent}/lib")
