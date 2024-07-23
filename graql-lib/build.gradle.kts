val kotlinVersion=project.properties.get("kotlinVersion")

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("io.micronaut.application") version "4.4.0"
    id("io.micronaut.aot") version "4.4.0"
}

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut.serde:micronaut-serde-processor")

    compileOnly("io.micronaut:micronaut-http-client")

    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("com.apollographql.federation:federation-graphql-java-support:5.1.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass = "com.thirtytwonineteen.graql.lib.GraqlLibMicronautTestApplication"
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}


// graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("com.thirtytwonineteen.graql.lib.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}



