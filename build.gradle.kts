plugins {
    kotlin("jvm") version "1.5.10"
    java
}

group = "kim.bifrost.rain.flandre"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("com.google.code.gson:gson:2.9.0")
    api("net.mamoe:mirai-core:2.9.2")
    api("net.mamoe:mirai-console:2.9.2")
    implementation(kotlin("stdlib"))
    testImplementation("net.mamoe:mirai-console-terminal:2.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}