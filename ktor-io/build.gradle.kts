import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.Family

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                api(project(":ktor-test-dispatcher"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().all {
        // println("NATIVE TARGET FOR IO: $this ${compilations["main"].defaultSourceSet.kotlin.srcDirs}")
        if (konanTarget.family == Family.ANDROID) {
            println("FOUND ANDROID $konanTarget")
            compilations["main"].also { compilation ->

                compilation.allKotlinSourceSets.forEach {
                    println("SOURCE SET: $it DEPENDS ON: ${it.dependsOn.map{it.name}}")
                }

                compilation.defaultSourceSet.dependencies {
                    implementation("org.danbrough.kotlinxtras:iconv:0.0.1-beta05")
                }

            }
        }
    }
}
