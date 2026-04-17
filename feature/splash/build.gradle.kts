plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.splash"
}

dependencies {
    implementation(projects.core.navigationContract)
}
