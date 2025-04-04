plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "cn.cotenite"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")

    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-tika-document-reader:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-milvus-store-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-mcp-server-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-mcp-client-webflux-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-cassandra-store-spring-boot-starter:1.0.0-M6")

    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.26.3")
    implementation("org.redisson:redisson-spring-boot-starter:3.44.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
    implementation("cn.hutool:hutool-all:5.8.21")
    implementation("cn.idev.excel:fastexcel:1.0.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:cassandra")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
