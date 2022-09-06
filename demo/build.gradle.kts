import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version Dependencies.kotlin
    // id("com.android.library")
}

group = "org.danbrough.ktor_demo"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral() //for release version
    maven(Dependencies.SONA_SNAPSHOTS) //for snapshots
    maven(Dependencies.SONA_STAGING)  //for pre-releases in the staging repo
}

val osName = System.getProperty("os.name")


kotlin {

    linuxX64()

    if (osName == "Mac OS X")
        macosX64()


    val commonMain by sourceSets.getting {
        dependencies {
            implementation(Dependencies.klog)
        }
    }

    targets.withType(KotlinNativeTarget::class).all {
        compilations["main"].apply {
            defaultSourceSet.dependsOn(nativeMain)
        }

        binaries {
            executable("ktorDemo1") {
                entryPoint = "demo1.main"
            }
        }
    }
}

