import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

description = "Ktor network utilities"

kotlin {
    createCInterop("network", nixTargets()) {
        defFile = projectDir.resolve("nix/interop/network.def")
    }

    sourceSets {
        jvmAndNixMain {
            dependencies {
                api(project(":ktor-utils"))
            }
        }

        jvmAndNixTest {
            dependencies {
                api(project(":ktor-test-dispatcher"))
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}

val linuxX64Main: KotlinSourceSet by kotlin.sourceSets.getting

kotlin.targets.withType<KotlinNativeTarget>().all {
    if (konanTarget != KonanTarget.LINUX_X64 && (konanTarget.family == Family.LINUX || konanTarget.family == Family.ANDROID)){
        //compilations["main"].defaultSourceSet.kotlin.srcDir("src/linuxX64/src")
        compilations["main"].defaultSourceSet.dependsOn(linuxX64Main)
    }
}


