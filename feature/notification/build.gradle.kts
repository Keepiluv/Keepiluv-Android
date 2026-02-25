plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.feature.notification"
}

dependencies {
    implementation(projects.core.notification)
}
