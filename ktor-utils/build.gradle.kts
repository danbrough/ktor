import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

kotlin {
    createCInterop("threadUtils", nixTargets()) {
        defFile = File(projectDir, "nix/interop/threadUtils.def")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":ktor-io"))
            }
        }
        commonTest {
            dependencies {
                api(project(":ktor-test-dispatcher"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().all {
        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID || konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX){
            compilations["main"].defaultSourceSet.kotlin.srcDir(file("linuxX64"))
        }
    }
}
