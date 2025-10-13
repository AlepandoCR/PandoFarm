plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.run-paper") version "2.3.1"

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.mapachos"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.9-R0.1-SNAPSHOT")
    implementation("net.essentialsx:EssentialsX:2.21.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("io.github.toxicity188:bettermodel:1.13.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    implementation(kotlin("reflect"))
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.yaml:snakeyaml:2.2")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }


    build {
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.21.8")
    }
}
