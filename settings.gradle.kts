import java.net.URI

include(":PluginGlide")


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url= URI("https://jitpack.io") }
        google()
    }
}

rootProject.name = "AnimationDrawable"
include(":app")
include(":FrameAnimation")
include(":APNG")
