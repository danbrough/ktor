import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                api(project(":ktor-test-dispatcher"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().all {
        println("NATIVE TARGET FOR IO: $this ${compilations["main"].defaultSourceSet.kotlin.srcDirs}")
        
    }
}
