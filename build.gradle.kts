plugins {
    alias(libs.plugins.kotlin.multiplatform) apply  false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}