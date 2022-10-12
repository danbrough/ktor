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

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID || konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX){
            compilations["main"].defaultSourceSet.kotlin.srcDir(file("linuxX64/src"))
            compilations["test"].defaultSourceSet.kotlin.srcDir(file("linuxX64/test"))
        }
    }
}
