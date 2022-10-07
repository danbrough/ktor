import org.jetbrains.kotlin.gradle.targets.native.tasks.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget


apply<test.server.TestServerPlugin>()

val WIN_LIBRARY_PATH =
    "c:\\msys64\\mingw64\\bin;c:\\tools\\msys64\\mingw64\\bin;C:\\Tools\\msys2\\mingw64\\bin"

val paths = if (HOST_NAME == "windows") {
    listOf(
        "C:/msys64/mingw64/include/curl",
        "C:/Tools/msys64/mingw64/include/curl",
        "C:/Tools/msys2/mingw64/include/curl"
    )
} else {
    listOf(
        "/opt/homebrew/opt/curl/include/curl",
        "/opt/local/include/curl",
        "/usr/local/include/curl",
        "/usr/include/curl",
        "/usr/local/opt/curl/include/curl",
        "/usr/include/x86_64-linux-gnu/curl",
        "/usr/local/Cellar/curl/7.62.0/include/curl",
        "/usr/local/Cellar/curl/7.63.0/include/curl",
        "/usr/local/Cellar/curl/7.65.3/include/curl",
        "/usr/local/Cellar/curl/7.66.0/include/curl",
        "/usr/local/Cellar/curl/7.80.0/include/curl",
        "/usr/local/Cellar/curl/7.80.0_1/include/curl",
        "/usr/local/Cellar/curl/7.81.0/include/curl"
    )
}

plugins {
    id("kotlinx-serialization")
}

kotlin {
    fastTarget()
    createCInterop("libcurl", listOf("macosX64", "macosArm64","linuxX64","linuxArm64" ,"linuxArm32Hfp","mingwX64")) {
        defFile = File(projectDir, "desktop/interop/libcurl.def")
        //includeDirs.headerFilterOnly(paths)
    }

    sourceSets {
        desktopMain {
            dependencies {
                api(project(":ktor-client:ktor-client-core"))
                api(project(":ktor-http:ktor-http-cio"))
            }
        }
        desktopTest {
            dependencies {
                api(project(":ktor-client:ktor-client-plugins:ktor-client-logging"))
                api(project(":ktor-client:ktor-client-plugins:ktor-client-json"))
            }
        }
    }

    afterEvaluate {
        if (HOST_NAME != "windows") return@afterEvaluate
        val winTests = tasks.findByName("mingwX64Test") as? KotlinNativeTest? ?: return@afterEvaluate
        winTests.environment("PATH", WIN_LIBRARY_PATH)
    }
}





val KonanTarget.platformName: String
  get() {
    if (family == Family.ANDROID) {
      return when (this) {
        KonanTarget.ANDROID_X64 -> "androidNativeX64"
        KonanTarget.ANDROID_X86 -> "androidNativeX86"
        KonanTarget.ANDROID_ARM64 -> "androidNativeArm64"
        KonanTarget.ANDROID_ARM32 -> "androidNativeArm32"
        else -> throw Error("Unhandled android target $this")
      }
    }
    return name.split("_").joinToString("") { it.capitalize() }.decapitalize()
  }

val generateInteropsDefTaskName = "generateInteropsDef"

tasks.register(generateInteropsDefTaskName) {
    description = "Generate desktop/interop/libcurl.def from desktop/interop/libcurl_headers.h"
    inputs.file("desktop/interop/libcurl_header.def")
    outputs.file("desktop/interop/libcurl.def")
    group = "interop"
    doFirst {
        val outputFile = outputs.files.files.first()
        println("Generating $outputFile")
        outputFile.printWriter().use { output ->
            output.println(inputs.files.files.first().readText())
            kotlin.targets.withType<KotlinNativeTarget>().forEach {
                val konanTarget = it.konanTarget
                output.println("compilerOpts.${konanTarget.name} = -Ibuild/kotlinxtras/curl/${konanTarget.platformName}/include/curl \\")
                output.println("\t-Ibuild/kotlinxtras/openssl/${konanTarget.platformName}/include/ \\")
                output.println("\t-I/usr/local/kotlinxtras/libs/curl/${konanTarget.platformName}/include/curl \\")
                output.println("\t-I/usr/local/kotlinxtras/libs/openssl/${konanTarget.platformName}/include/ ")

                output.println("linkerOpts.${konanTarget.name} = -Lbuild/kotlinxtras/curl/${konanTarget.platformName}/lib \\")
                output.println("\t-Lbuild/kotlinxtras/openssl/${konanTarget.platformName}/lib \\")
                output.println("\t-L/usr/local/kotlinxtras/libs/curl/${konanTarget.platformName}/lib \\")
                output.println("\t-L/usr/local/kotlinxtras/libs/openssl/${konanTarget.platformName}/lib ")

                output.println("libraryPaths.${konanTarget.name} = build/kotlinxtras/curl/${konanTarget.platformName}/lib \\")
                output.println("\tbuild/kotlinxtras/openssl/${konanTarget.platformName}/lib \\")
                output.println("\t/usr/local/kotlinxtras/libs/curl/${konanTarget.platformName}/lib \\")
                output.println("\t/usr/local/kotlinxtras/libs/openssl/${konanTarget.platformName}/lib ")
                output.println()

            }
        }
    }
}
