import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.twix.android.application)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.twix.kermit)
}

val localPropertiesFile = project.rootProject.file("local.properties")
val properties =
    Properties().apply {
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

android {
    namespace = "com.yapp.twix"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.yapp.twix"
        versionCode = 9
        versionName = "1.0.1"

        val kakaoKey = properties["kakao_dev_native_app_key"].toString()
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey.trim('"')
        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"$kakaoKey\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.core.designSystem)
    implementation(projects.core.network)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)
    implementation(projects.core.datastore)
    implementation(projects.core.util)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.feature.login)
    implementation(projects.feature.main)
    implementation(projects.feature.photolog.capture)
    implementation(projects.feature.photolog.detail)
    implementation(projects.feature.photolog.editor)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.goalEditor)
    implementation(projects.feature.goalManage)
    implementation(projects.feature.settings)
    implementation(projects.feature.stats.detail)
    implementation(projects.core.notification)
    implementation(projects.core.share)
    implementation(projects.core.navigationContract)
    implementation(projects.feature.notification)
    implementation(projects.feature.splash)

    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.messaging)

    implementation(libs.kakao.user)
}
