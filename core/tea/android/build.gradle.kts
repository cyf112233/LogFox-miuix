plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.compose)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    namespace = "com.f0x1d.logfox.core.tea.android"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.tea.base)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.runtime)
    api(libs.androidx.fragment)
    api(libs.androidx.preference)
    api(libs.material)

    api(projects.core.ui.base)
    api(projects.core.ui.compose.designSystem)
    implementation(libs.androidx.compose.runtime)
}
