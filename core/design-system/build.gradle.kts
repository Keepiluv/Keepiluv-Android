plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.android.compose)
}

android {
    namespace = "com.twix.designsystem"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.ui)
}
