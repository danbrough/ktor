/*
 * Copyright 2014-2022 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

kotlin {
    sourceSets {
        posixMain {
            dependencies {
                implementation(project(":ktor-utils"))
            }
        }
    }

      targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID || konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX){
            compilations["main"].defaultSourceSet.kotlin.srcDir(file("linuxX64/src"))
        }
    }
}
