plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.twix.kermit)
}

android {
    namespace = "com.twix.notification"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.result)
    implementation(projects.core.deviceContract)
    implementation(projects.core.navigationContract)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.messaging)
}
