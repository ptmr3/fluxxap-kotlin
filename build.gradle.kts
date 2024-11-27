plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    `java-library`
    `maven-publish`
}

group = "com.ptmr3"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinpoet.ksp)
    testImplementation(libs.ksp)
    testImplementation(libs.ksp.api)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.kotlin.compile.testing.ksp)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ptmr3"
            artifactId = "fluxxap"
            version = "0.3"

            from(components["kotlin"])
        }
    }
}

kotlin {
    jvmToolchain(17)
}