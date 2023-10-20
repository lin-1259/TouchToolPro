import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.nav.safeargs)
}

android {
    namespace = "top.bogey.touch_tool_pro"
    compileSdk = 34
    ndkVersion = "21.4.7075529"
    buildToolsVersion = "34.0.0"

    val pattern = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
    val now = LocalDateTime.now().format(pattern)

    defaultConfig {
        applicationId = "top.bogey.touch_tool_pro"
        minSdk = 24
        targetSdk = 34
        versionCode = 38
        versionName = now

        externalNativeBuild {
            cmake {
                cppFlags.add("-std=c++14")
                cppFlags.add("-frtti")
                cppFlags.add("-fexceptions")
                cppFlags.add("-Wno-format")

                arguments.add("-DANDROID_PLATFORM=android-23")
                arguments.add("-DANDROID_STL=c++_shared")
                arguments.add("-DANDROID_ARM_NEON=TRUE")

                abiFilters.add("arm64-v8a")
            }
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += "arm64-v8a"
        }
    }

    buildTypes {
        release {
            applicationIdSuffix = ".beta"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "app_name", "@string/app_name_release")
        }

        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "app_name", "@string/app_name_debug")
        }
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            if (buildType.name == "release") {
                val impl = this as BaseVariantOutputImpl
                impl.outputFileName = "点击助手Pro_$now.apk"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf(Pair("include", listOf("*.jar")), Pair("dir", "libs"))))

    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)
    implementation(libs.work.runtime)

    implementation(libs.mmkv)
    implementation(libs.gson)
    implementation(libs.treeview)

    implementation(platform(libs.kotlin.bom))
}