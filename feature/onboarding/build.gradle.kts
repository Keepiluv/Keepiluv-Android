plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.onboarding"
}

dependencies {
    implementation(project(":core:navigation-contract"))
}
