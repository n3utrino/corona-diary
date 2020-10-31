import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}


repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/") // soon will be just jcenter()
}

dependencies {

}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
            }
        }
    }
    jvm() {
    }
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}

