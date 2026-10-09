plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

android.namespace = "com.f0x1d.logfox.feature.crashes.presentation"

dependencies {
    implementation(projects.feature.crashes.api)
    implementation(projects.feature.appsPicker.api)
    implementation(projects.feature.database.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.core.ui.icons)
    implementation(projects.core.tea.android)
    implementation(projects.core.di)
    implementation(projects.core.compat)
    implementation(projects.core.context)
    implementation(projects.core.copy)
    implementation(projects.feature.datetime.api)
    implementation(projects.feature.navigation.api)

    implementation(projects.strings)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.flow.preferences)
}
