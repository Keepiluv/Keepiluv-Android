plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.photolog.detail"
}

dependencies {
    implementation(projects.feature.photolog.capture)
    implementation(projects.feature.photolog.editor)
}
