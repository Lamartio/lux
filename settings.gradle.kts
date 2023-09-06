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

rootProject.name = "Lux"
includeBuild("convention-plugins")

":lib".also { path ->
    include(path)
    project(path).name = "lux"
}