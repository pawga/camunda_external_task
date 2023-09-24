import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.9.20-Beta2"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.example"
version = "0.0.1"
description = "demo-external-task-01"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-external-task-client:7.20.0-alpha5")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation(kotlin("stdlib-jdk8"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}