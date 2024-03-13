plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "kr.foundcake"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exposedVersion = "0.48.0"
dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    // JDA
    implementation("net.dv8tion:JDA:5.0.0-beta.20") {
        exclude(module="opus-java")
    }
    implementation("club.minnced:jda-ktx:0.11.0-beta.20")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    // DB
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.mysql:mysql-connector-j:8.2.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks{
    shadowJar{
        archiveFileName.set("RoleAuth.jar")
        manifest{
            attributes(mapOf("Main-Class" to "kr.foundcake.role_auth.MainKt"))
        }
    }
}