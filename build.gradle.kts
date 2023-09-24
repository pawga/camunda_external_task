
plugins {
    java
}

group = "com.example"
version = "0.0.1"
description = "demo-external-task-01"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
}

dependencies {
    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-external-task-client:7.20.0-alpha5")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
}
