val ktorVersion: String by project
val appClassName = "team.yi.ktor.sample.AppKt"

plugins {
    application

    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":ktor-banner"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("com.diogonunes:JColor:5.0.1")
}

application {
    mainClass.set(appClassName)

    applicationDefaultJvmArgs = listOf(
        "-XX:MetaspaceSize=128m",
        "-XX:MaxMetaspaceSize=128m",
        "-Xms128m",
        "-Xmx128m",
        "-Xmn128m",
        "-Xss256k",
        "-XX:SurvivorRatio=8"
    )
}

tasks {
    jar { enabled = true }

    shadowJar {
        project.setProperty("mainClassName", appClassName)

        mergeServiceFiles()
    }
}
