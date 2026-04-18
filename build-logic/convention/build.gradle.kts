plugins {
    `kotlin-dsl`
}

group = "com.twix.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.android.junit5.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication"){
            id = "twix.android.application"
            implementationClass = "com.twix.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "twix.android.library"
            implementationClass = "com.twix.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose"){
            id = "twix.android.compose"
            implementationClass = "com.twix.convention.AndroidComposeConventionPlugin"
        }
        register("feature"){
            id = "twix.feature"
            implementationClass = "com.twix.convention.FeatureConventionPlugin"
        }
        register("koin"){
            id = "twix.koin"
            implementationClass = "com.twix.convention.KoinConventionPlugin"
        }
        register("javaLibrary"){
            id = "twix.java.library"
            implementationClass = "com.twix.convention.JvmLibraryConventionPlugin"
        }
        register("data"){
            id = "twix.data"
            implementationClass = "com.twix.convention.DataConventionPlugin"
        }
        register("kermit") {
            id = "twix.kermit"
            implementationClass = "com.twix.convention.KermitConventionPlugin"
        }
    }
}
