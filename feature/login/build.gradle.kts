import java.util.Properties
import kotlin.apply

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
    namespace = "com.twix.login"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            "\"${localProperties.getProperty("google_client_id")}\"",
        )
    }
}
dependencies {
    implementation(libs.googleid)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
}
