import java.util.Properties

plugins {
    alias(libs.plugins.twix.feature)
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

android {
    namespace = "com.twix.settings"

    buildFeatures {
        buildConfig = true
    }

    val privacyPolicyUrl =
        localProperties.getProperty("privacy_policy_url")
            ?: providers.gradleProperty("privacy_policy_url").orNull
            ?: "https://incongruous-sweatshirt-b32.notion.site/Keepliuv-3024eb2e10638051824ef9ac7f9a522f"

    buildTypes {
        all {
            buildConfigField("String", "PRIVACY_POLICY_URL", "\"$privacyPolicyUrl\"")
        }
    }
}

dependencies {
    implementation(projects.core.notification)

    implementation(libs.browser)
}
