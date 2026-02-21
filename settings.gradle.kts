pluginManagement {
    includeBuild("build-logic")
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
        google()
        mavenCentral()
    }
}

rootProject.name = "Twix"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":domain")
include(":data")
include(":feature:login")
include(":core:datastore")
include(":core:token")
include(":core:util")
include(":core:ui")
include(":core:result")
include(":core:navigation")
include(":core:design-system")
include(":core:network")
include(":core:analytics")
include(":feature:main")
include(":feature:task-certification")
include(":feature:goal-editor")
include(":feature:goal-manage")
include(":feature:settings")
include(":feature:onboarding")
