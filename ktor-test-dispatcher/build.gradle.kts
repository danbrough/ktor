/*
 * Copyright 2014-2022 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget


kotlin {
    sourceSets {
        posixMain {
            dependencies {
                implementation(project(":ktor-utils"))
            }
        }
    }
}



val linuxX64Main: KotlinSourceSet by kotlin.sourceSets.getting
val linuxX64Test: KotlinSourceSet by kotlin.sourceSets.getting

kotlin.targets.withType<KotlinNativeTarget>().all {
    if (konanTarget != KonanTarget.LINUX_X64 && (konanTarget.family == Family.LINUX || konanTarget.family == Family.ANDROID)){
        compilations["main"].defaultSourceSet.dependsOn(linuxX64Main)
        compilations["test"].defaultSourceSet.dependsOn(linuxX64Test)
    }
}
