package com.twix.convention

import com.twix.convention.extension.implementation
import com.twix.convention.extension.library
import com.twix.convention.extension.libs
import org.gradle.kotlin.dsl.dependencies

class KermitConventionPlugin: BuildLogicConventionPlugin({
    dependencies {
        implementation(libs.library("kermit"))
    }
})