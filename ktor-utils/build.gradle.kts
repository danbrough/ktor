import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
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
}

val linuxX64Main: KotlinSourceSet by kotlin.sourceSets.getting

kotlin.targets.withType<KotlinNativeTarget>().all {
    if (konanTarget != KonanTarget.LINUX_X64 && (konanTarget.family == Family.LINUX || konanTarget.family == Family.ANDROID)){
        compilations["main"].defaultSourceSet.dependsOn(linuxX64Main)
    }
}


