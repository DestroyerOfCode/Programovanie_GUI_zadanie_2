import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "org.paneurouni"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    commonMainImplementation("com.google.code.gson:gson:2.10.1")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                val lifecycle_version = "2.6.1"
                val arch_version = "2.2.0"

                // ViewModel
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
                // ViewModel utilities for Compose
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
                // LiveData
                implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
                // Lifecycles only (without ViewModel or LiveData)
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
                // Lifecycle utilities for Compose
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

                // Saved state module for ViewModel
                implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

                // optional - helpers for implementing LifecycleOwner in a Service
                implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")

                // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
                implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")

                // optional - ReactiveStreams support for LiveData
                implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")

                // optional - Test helpers for LiveData
                implementation("androidx.arch.core:core-testing:$arch_version")

                // optional - Test helpers for Lifecycle runtime
                implementation ("androidx.lifecycle:lifecycle-runtime-testing:$lifecycle_version")

            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Zadanie2"
            packageVersion = "1.0.0"
        }
    }
}
