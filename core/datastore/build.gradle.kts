plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.twix.datastore"
}

dependencies {
    implementation(projects.core.token)
    implementation(projects.core.deviceContract)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
}
