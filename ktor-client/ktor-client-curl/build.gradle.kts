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
} else {
    listOf(
        "../../libs/curl/curl-7_85_0/linuxX64/include/"
    )
}

plugins {
    id("kotlinx-serialization")
}

kotlin {
    fastTarget()

    createCInterop("libcurl", listOf("macosX64", "linuxX64","linuxArm64" ,"mingwX64")) {
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

    afterEvaluate {
        if (HOST_NAME != "windows") return@afterEvaluate
        val winTests = tasks.findByName("mingwX64Test") as? KotlinNativeTest? ?: return@afterEvaluate
        winTests.environment("PATH", WIN_LIBRARY_PATH)
    }
}
