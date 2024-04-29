pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "dagger.hilt.android.plugin") {
            useModule("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BookRecord"
include(":app")
//
