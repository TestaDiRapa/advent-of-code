val ktorVersion = "2.3.4"

plugins {
    id("io.ktor.plugin") version "2.3.4"
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "io.ktor", name = "ktor-client-cio-jvm", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
}

application {
    mainClass.set("MainKt")
}