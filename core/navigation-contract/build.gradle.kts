plugins {
    alias(libs.plugins.twix.android.library)
}

android {
    namespace = "com.twix.navigation_contract"
}

dependencies {
    implementation(project(":core:ui"))
}
