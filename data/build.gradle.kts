plugins {
    alias(libs.plugins.twix.data)
}

android {
    namespace = "com.twix.data"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.result)
    implementation(projects.core.token)
}
