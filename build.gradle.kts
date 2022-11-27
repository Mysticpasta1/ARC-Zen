plugins {
    java

    //Gradle shadow plugin to make fatjar
    id("com.github.johnrengelman.shadow") version ("7.0.0")
}

group = "com.mystic.arczen"
version = "2022.11.27"

repositories {
    mavenCentral()
}

sourceSets {
    all {
        dependencies {
            implementation("com.discord4j:discord4j-core:3.2.0")
            implementation("ch.qos.logback:logback-classic:1.2.3")
            implementation("org.apache.httpcomponents:httpclient:4.5.13")
            implementation("org.json:json:20220924")
        }
    }
}

/*
Configure the sun.tools.jar.resources.jar task for our main class and so that `./gradlew build` always makes the fatjar
This boilerplate is completely removed when using Springboot
 */
tasks.jar {
    manifest {
        attributes("Main-Class" to "com.mystic.arczen.ARC-Zen")
    }

    finalizedBy("shadowJar")
}