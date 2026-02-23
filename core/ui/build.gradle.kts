plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.android.compose)
    alias(libs.plugins.twix.kermit)
    alias(libs.plugins.twix.koin)
}

android {
    namespace = "com.twix.ui"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.result)
    implementation(libs.androidx.exifinterface)
}
