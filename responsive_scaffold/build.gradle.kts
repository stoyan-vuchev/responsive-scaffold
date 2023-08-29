@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    `maven-publish`
}

android {

    namespace = "com.stoyanvuchev.responsive_scaffold"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {

                groupId = "com.github.stoyan-vuchev"
                artifactId = "responsive-scaffold"
                version = "1.0.1"

                afterEvaluate {
                    from(components["release"])
                }

            }
        }
    }
}

dependencies {

    // Jetpack Compose

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.material3.windowSizeClass)

    // Testing

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.manifest)
    androidTestImplementation(libs.ui.test.junit4)

}