val mockkVersion = "1.14.7"
val springAIVersion = "2.0.0-M1"
val kotlinxSerializationVersion = "1.9.0"
val caffeineVersion = "3.2.3"
plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.18.0"
    kotlin("plugin.serialization") version "2.3.0"
}

group = "net.battaglini"
version = "0.0.1-SNAPSHOT"
description = "An MCP server that queries the PTV API for information about routes"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

openApiGenerate {
    generatorName = "kotlin"
    inputSpec = file("src/main/resources/api/ptv-v3-api.json").absolutePath
    outputDir = "$buildDir/generated/src/main/kotlin"
    apiPackage = "au.gov.vic.ptv.timetableapi.api"
    modelPackage = "au.gov.vic.ptv.timetableapi.model"
    configOptions = mapOf(
        Pair("dateLibrary", "kotlinx-datetime"),
        Pair("serializationLibrary", "kotlinx_serialization"),
        Pair(
            "additionalModelTypeAnnotations",
            "@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class);@kotlinx.serialization.json.JsonIgnoreUnknownKeys"
        )
    )
    globalProperties = mapOf(
        Pair("models", "")
    )
    generateApiTests = false
    generateModelTests = false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAIVersion")
        mavenBom("org.jetbrains.kotlinx:kotlinx-serialization-bom:$kotlinxSerializationVersion")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
    sourceSets.main {
        kotlin.srcDir("$buildDir/generated/src/main/kotlin")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named { it == "build" || it == "bootRun" }.forEach { it.dependsOn("openApiGenerate") }
tasks.named("compileKotlin") {
    mustRunAfter(tasks.named("openApiGenerate"))
}
