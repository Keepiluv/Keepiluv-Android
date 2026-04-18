package com.twix.convention

import com.twix.convention.extension.implementation
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : BuildLogicConventionPlugin({
    apply<AndroidLibraryConventionPlugin>()
    apply<KoinConventionPlugin>()
    apply<AndroidComposeConventionPlugin>()
    apply< KermitConventionPlugin>()

    dependencies {
        implementation(project(":core:analytics"))
        implementation(project(":core:design-system"))
        implementation(project(":core:navigation"))
        implementation(project(":core:ui"))
        implementation(project(":core:util"))
        implementation(project(":core:result"))
        implementation(project(":domain"))
    }
})
