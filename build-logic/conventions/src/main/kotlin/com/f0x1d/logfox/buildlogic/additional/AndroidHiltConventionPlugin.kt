package com.f0x1d.logfox.buildlogic.additional

import com.f0x1d.logfox.buildlogic.extensions.implementation
import com.f0x1d.logfox.buildlogic.extensions.ksp
import com.f0x1d.logfox.buildlogic.extensions.library
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import com.f0x1d.logfox.buildlogic.extensions.versionString
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("ksp"))
            apply(pluginId("hilt-android"))
        }

        // Hilt's annotation processor reads Kotlin class metadata; its bundled reader only supports
        // up to 2.3.x while some dependencies (Miuix) are built with Kotlin 2.4. Force a newer reader
        // so the aggregating task does not fail on those classes.
        configurations.configureEach {
            resolutionStrategy.force(
                "org.jetbrains.kotlin:kotlin-metadata-jvm:${versionString("kotlinMetadata")}",
            )
        }

        dependencies {
            implementation(library("hilt-android"))
            ksp(library("hilt-compiler"))
        }
    }
}
