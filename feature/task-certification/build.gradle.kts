plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.task_certification"
}
dependencies {
    implementation(projects.core.util)

    implementation(libs.bundles.cameraX)
    implementation(libs.guava)
    implementation(libs.kotlinx.serialization.json)
}
