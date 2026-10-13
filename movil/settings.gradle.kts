// Proyecto Android de Los Hilos de Maya: cliente móvil de la misma API PHP que usa la app React
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LosHilosDeMaya"
include(":app")
