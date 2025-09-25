plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.funkatronics.kborsh"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
    publishing { singleVariant("release") }
}

kotlin {
    jvmToolchain(11)

    jvm()
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kborsh"
        }
    }
//    js {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.multimult)
                implementation(libs.buffer)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val multiplatformMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting { dependsOn(multiplatformMain) }
        val androidMain by getting { dependsOn(multiplatformMain) }

        val iosX64Main by getting { dependsOn(multiplatformMain) }
        val iosArm64Main by getting { dependsOn(multiplatformMain) }
        val iosSimulatorArm64Main by getting { dependsOn(multiplatformMain) }

        val macosX64Main by getting { dependsOn(multiplatformMain) }
        val macosArm64Main by getting { dependsOn(multiplatformMain) }

//        val jsMain by getting { dependsOn(multiplatformMain) }
//        val nativeMain by getting { dependsOn(multiplatformMain) }
    }
}

mavenPublishing {
    val artifactId: String by project
    coordinates(group as String, artifactId, version as String)
}
