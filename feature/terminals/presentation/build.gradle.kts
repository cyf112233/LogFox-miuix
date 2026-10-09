plugins {
    alias(libs.plugins.logfox.android.feature)
}

android {
    namespace = "com.f0x1d.logfox.feature.terminals.presentation"

    // `aidl` is off because the SDK's native aidl binary SIGILLs on some aarch64/PRoot
    // environments; `src/main/java/.../IUserService.java` is the committed pre-generated
    // equivalent of `src/main/aidl/.../IUserService.aidl`.
    buildFeatures.aidl = false
}

dependencies {
    implementation(projects.feature.terminals.api)

    implementation(libs.kotlinx.coroutines.core)
}
