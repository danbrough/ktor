/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jmailen.gradle.kotlinter.*

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    configure(block)
}

val Project.kotlin: KotlinMultiplatformExtension get() = the()

val Project.kotlinter: KotlinterExtension get() = the()

fun KotlinMultiplatformExtension.createCInterop(
    name: String,
    cinteropTargets: List<String>,
    block: DefaultCInteropSettings.() -> Unit
) {
    cinteropTargets.mapNotNull { targets.findByName(it) }.filterIsInstance<KotlinNativeTarget>().forEach {
        val main by it.compilations
        main.cinterops.create(name, block)
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmAndNixMain(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("jvmAndNixMain") ?: getByName("jvmMain")
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmAndNixTest(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("jvmAndNixTest") ?: getByName("jvmTest")
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.posixMain(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("posixMain") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.darwinMain(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("darwinMain") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.darwinTest(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("darwinTest") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jsMain(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("jsMain") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jsTest(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("jsTest") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.desktopMain(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("desktopMain") ?: return
    block(sourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.desktopTest(block: KotlinSourceSet.() -> Unit) {
    val sourceSet = findByName("desktopTest") ?: return
    block(sourceSet)
}

