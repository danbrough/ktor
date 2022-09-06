import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  //google()
}

kotlinDslPluginOptions {
  jvmTarget.set(provider { java.targetCompatibility.toString() })
}
tasks.withType(KotlinCompile::class) {
  kotlinOptions {
    jvmTarget = "11"
  }
}

