import java.util.Properties

plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.ksp)
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

android {
    namespace = "com.twix.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            val devBaseUrl =
                localProperties.getProperty("dev_base_url")
                    ?: error("dev_base_url is missing in local.properties")
            buildConfigField("String", "BASE_URL", "\"$devBaseUrl\"")
        }
        release {
            val prodBaseUrl =
                localProperties.getProperty("prod_base_url")
                    ?: error("prod_base_url is missing in local.properties")
            buildConfigField("String", "BASE_URL", "\"$prodBaseUrl\"")
        }
    }
}

dependencies {
    implementation(projects.core.token)
    implementation(projects.core.result)
    implementation(projects.domain)

    implementation(libs.bundles.ktor)
    implementation(libs.ktorfit.lib)
    ksp(libs.ktorfit.ksp)
}
