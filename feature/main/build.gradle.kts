plugins {
    alias(libs.plugins.twix.feature)
}

android {
    namespace = "com.twix.main"
}

dependencies {
    implementation(libs.compose.coil)
    implementation(libs.compose.coil.network)
}
