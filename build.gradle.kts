plugins {
    java
    `maven-publish`
    alias(libs.plugins.lavalink)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val pluginVersion = "0.0.5"
val commitSha = System.getenv("GITHUB_SHA")?.take(7) ?: "unknown"
val preRelease = System.getenv("PRERELEASE")?.toBoolean() ?: false
val verName = if (preRelease) "$commitSha" else pluginVersion

group = "me.devoxin.lavadspx.plugin"
version = verName

lavalinkPlugin {
    name = "lavadspx-plugin"
    apiVersion = libs.versions.lavalink.api
    serverVersion = libs.versions.lavalink.server
    configurePublishing = false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.Devoxin:LavaDSPX:2.0.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    
    jar {
        enabled = false
    }
    
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }
    
    build {
        dependsOn(shadowJar)
    }
}

publishing {
    repositories {
        maven {
            url = if (preRelease) {
                uri("https://maven.appujet.site/snapshots")
            } else {
                uri("https://maven.appujet.site/releases")
            }
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.shadowJar.get())
        }
    }
}