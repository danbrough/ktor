import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.konan.target.*

plugins {
    id("org.danbrough.kotlinxtras.consumer") version "0.0.3-beta01"
}



kotlin {
    sourceSets {
        commonTest {
            dependencies {
                api(project(":ktor-test-dispatcher"))
            }
        }
    }


    targets.withType<KotlinNativeTarget>().filter { it.konanTarget.family == Family.ANDROID }.forEach {

        it.compilations["main"].apply {
            println("CONFIGURING ANDROID COMPILATION $this")
            dependencies {
                implementation("org.danbrough.kotlinxtras:iconv:1.17b")
            }
        }
    }
}
