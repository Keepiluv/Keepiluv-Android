plugins {
    alias(libs.plugins.twix.android.application)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.yapp.twix"

    defaultConfig {
        applicationId = "com.yapp.twix"
        versionCode = 1
        versionName = "1.0"
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
    implementation(projects.feature.taskCertification)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.goalEditor)
    implementation(projects.feature.goalManage)
    implementation(projects.feature.settings)

    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
}
