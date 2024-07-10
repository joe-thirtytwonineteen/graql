import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = project.properties.get("kotlinVersion")
val graphQLJavaVersion = project.properties.get("graphQLJavaVersion")

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.test-resources") version "4.4.0"
    id("io.micronaut.aot") version "4.4.0"
    idea
}

buildscript {
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

group = "com.thirtytwonineteen.graql"


allprojects {
    group = "com.thirtytwonineteen.graql"
    repositories {
        mavenCentral()
    }
}

configure( subprojects ) {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("com.google.devtools.ksp")
        plugin("com.github.johnrengelman.shadow")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    
    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}