import org.jetbrains.kotlin.gradle.targets.native.tasks.*

apply<test.server.TestServerPlugin>()

val WIN_LIBRARY_PATH =
    "c:\\msys64\\mingw64\\bin;c:\\tools\\msys64\\mingw64\\bin;C:\\Tools\\msys2\\mingw64\\bin"

val paths = if (HOST_NAME == "windows") {
    listOf(
        "C:/msys64/mingw64/include/curl",
        "C:/Tools/msys64/mingw64/include/curl",
        "C:/Tools/msys2/mingw64/include/curl"
    )
} else emptyList()
+

plugins {
    id("kotlinx-serialization")
}

kotlin {
    if (fastTarget()) return@kotlin

    createCInterop("libcurl", listOf("macosX64", "linuxX64","linuxArm64","linuxArm32Hfp", "mingwX64")) {
        defFile = File(projectDir, "desktop/interop/libcurl.def")
        includeDirs.headerFilterOnly(paths)
    }

    createCInterop("libcurl", listOf("macosArm64")) {
        defFile = File(projectDir, "desktop/interop/libcurl_arm64.def")
        includeDirs.headerFilterOnly(paths)
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


    tasks.findByName("linkDebugTestMingwX64")?.onlyIf { false }
}
