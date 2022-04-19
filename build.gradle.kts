plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    java
}

group = "kim.bifrost.rain.flandre"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1-native-mt")
    implementation("com.google.code.gson:gson:2.9.0")
    api("net.mamoe:mirai-core:2.10.1")
    api("net.mamoe:mirai-console:2.10.1")
    implementation(kotlin("stdlib"))
    testImplementation("net.mamoe:mirai-console-terminal:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}