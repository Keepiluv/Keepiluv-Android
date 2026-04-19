plugins {
    alias(libs.plugins.twix.android.library)
    alias(libs.plugins.twix.koin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.twix.database"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
