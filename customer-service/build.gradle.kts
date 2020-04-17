import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

application {
    mainClassName = "com.github.eventdrivenecomm.customerservice.AppKt"
}

group = "com.github.eventdrivenecomm.customerservice"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    /////////////////////
    /// Versions
    /////////////////////
    val exposedVersion = "0.23.1"
    val h2Version = "1.4.200"
    val hikariCPVersion = "3.4.2"
    val konfigVersion = "1.6.10.0"
    val pgVersion = "42.2.12"
    val jwtVersion = "3.10.2"

    /////////////////////
    /// Main dependencies
    /////////////////////
    implementation(kotlin("stdlib-jdk8"))
    
    //javalin
    implementation("io.javalin:javalin:3.7.0")

    //koin
    compile("org.koin:koin-core:2.1.3")

    //jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")

    //slf4j-simple
    implementation("org.slf4j:slf4j-simple:1.7.25")

    //exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    //hikariCP
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")

    //h2
    implementation("com.h2database:h2:$h2Version")

    //postgres driver
    implementation("org.postgresql:postgresql:$pgVersion")

    //JWT
    implementation("com.auth0:java-jwt:$jwtVersion")

    //swagger TODO:

    //konfig
    implementation("com.natpryce:konfig:$konfigVersion")
    
    /////////////////////
    /// Test dependencies
    /////////////////////
    testCompile("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    shadowJar {
        archiveFileName.set("customer-service.jar")
        destinationDirectory.set(file("./target"))
    }
}
