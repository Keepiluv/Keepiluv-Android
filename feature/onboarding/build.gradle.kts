plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.onboarding"
}

dependencies {
    implementation(projects.core.navigationContract)
    implementation(projects.core.share)
}
