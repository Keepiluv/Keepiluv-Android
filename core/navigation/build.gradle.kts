plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.android.compose)
    alias(libs.plugins.twix.koin)
}

android {
    namespace = "com.twix.navigation"
}

dependencies {
    implementation(projects.core.navigationContract)
    implementation(projects.core.ui)
    implementation(projects.domain)
}
