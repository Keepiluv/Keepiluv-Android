plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.koin)
}

android {
    namespace = "com.twix.share"
}

dependencies {
    implementation(projects.core.navigationContract)
}
