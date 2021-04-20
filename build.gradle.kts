val kotlinVersion: String by project
val ktorVersion: String by project

object Constants {
    const val gitUrl = "github.com"
    const val gitProjectUrl = "ymind/ktor-banner"

    const val projectVersion = "0.1.0-SNAPSHOT"
}

plugins {
    `maven-publish`
    signing

    kotlin("jvm")

    // https://plugins.gradle.org/plugin/team.yi.semantic-gitlog
    id("team.yi.semantic-gitlog") version "0.5.17"

    // https://plugins.gradle.org/plugin/se.patrikerdes.use-latest-versions
    id("se.patrikerdes.use-latest-versions") version "0.2.16"
    // https://plugins.gradle.org/plugin/com.github.ben-manes.versions
    id("com.github.ben-manes.versions") version "0.38.0"

    // https://plugins.gradle.org/plugin/org.jlleitschuh.gradle.ktlint
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    // https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
}

group = "team.yi.ktor"
version = Constants.projectVersion
description = "Add a banner for ktor."

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "kotlin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")

    version = Constants.projectVersion

    ktlint {
        debug.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)

        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config = files("$rootDir/config/detekt/detekt.yml")
        baseline = file("$rootDir/config/detekt/baseline.xml")

        reports {
            html.enabled = true
            xml.enabled = true
            txt.enabled = true
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven { setUrl("https://jcenter.bintray.com") }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

        testImplementation(platform("org.junit:junit-bom:5.7.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        val kotlinSettings: org.jetbrains.kotlin.gradle.tasks.KotlinCompile.() -> Unit = {
            kotlinOptions.languageVersion = "1.4"
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
            kotlinOptions.javaParameters = true
            kotlinOptions.freeCompilerArgs += listOf(
                "-Xjsr305=strict"
            )
        }

        compileKotlin(kotlinSettings)
        compileTestKotlin(kotlinSettings)
        compileJava { options.encoding = Charsets.UTF_8.name() }
        compileTestJava { options.encoding = Charsets.UTF_8.name() }
        javadoc { options.encoding = Charsets.UTF_8.name() }
        test {
            useJUnitPlatform()

            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    if (project == rootProject) return@allprojects
    if (project.name != "ktor-banner") return@allprojects

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])

                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }

                pom {
                    group = rootProject.group
                    name.set(project.name)
                    description.set(project.description)
                    url.set("https://${Constants.gitUrl}/${Constants.gitProjectUrl}")
                    inceptionYear.set("2021")

                    scm {
                        url.set("https://${Constants.gitUrl}/${Constants.gitProjectUrl}")
                        connection.set("scm:git:git@${Constants.gitUrl}:${Constants.gitProjectUrl}.git")
                        developerConnection.set("scm:git:git@${Constants.gitUrl}:${Constants.gitProjectUrl}.git")
                    }

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }

                    organization {
                        name.set("Yi.Team")
                        url.set("https://yi.team/")
                    }

                    developers {
                        developer {
                            name.set("ymind")
                            email.set("ymind@yi.team")
                            url.set("https://yi.team/")
                            organization.set("Yi.Team")
                            organizationUrl.set("https://yi.team/")
                        }
                    }

                    issueManagement {
                        system.set("GitHub")
                        url.set("https://${Constants.gitUrl}/${Constants.gitProjectUrl}/issues")
                    }

                    ciManagement {
                        system.set("GitHub")
                        url.set("https://${Constants.gitUrl}/${Constants.gitProjectUrl}/actions")
                    }
                }
            }
        }

        repositories {
            maven {
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

                url = if (version.toString().endsWith("SNAPSHOT", true)) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = System.getenv("OSSRH_USERNAME") ?: "${properties["OSSRH_USERNAME"]}"
                    password = System.getenv("OSSRH_TOKEN") ?: "${properties["OSSRH_TOKEN"]}"
                }
            }
        }
    }

    signing {
        val secretKeyRingFile = System.getenv("OSSRH_GPG_SECRET_KEY") ?: "${properties["OSSRH_GPG_SECRET_KEY"]}"

        extra.set("signing.keyId", System.getenv("OSSRH_GPG_SECRET_ID") ?: "${properties["OSSRH_GPG_SECRET_ID"]}")
        extra.set("signing.secretKeyRingFile", "${rootDir.absolutePath}/$secretKeyRingFile")
        extra.set("signing.password", System.getenv("OSSRH_GPG_SECRET_PASSWORD") ?: "${properties["OSSRH_GPG_SECRET_PASSWORD"]}")

        sign(publishing.publications.getByName("mavenJava"))
    }
}

tasks {
    create<Delete>("cleanup") {
        delete("$rootDir/logs", "$rootDir/tmp", "$rootDir/src")

        rootProject.childProjects.forEach { (name, _) ->
            delete("$rootDir/$name/logs", "$rootDir/$name/tmp")
        }
    }

    clean { dependsOn(":cleanup") }
    compileJava { dependsOn(":ktlintFormat") }

    ktlintFormat {
        outputs.upToDateWhen { false }
    }

    ktlintCheck {
        outputs.upToDateWhen { false }
    }

    val gitlogFileSets = setOf(
        team.yi.gradle.plugin.FileSet(
            file("${rootProject.rootDir}/config/gitlog/CHANGELOG.md.mustache"),
            file("${rootProject.rootDir}/CHANGELOG.md")
        ),
        team.yi.gradle.plugin.FileSet(
            file("${rootProject.rootDir}/config/gitlog/CHANGELOG_zh-cn.md.mustache"),
            file("${rootProject.rootDir}/CHANGELOG_zh-cn.md")
        )
    )
    val gitlogLocaleProfiles = mapOf(
        "zh-cn" to file("${rootProject.rootDir}/config/gitlog/commit-locales_zh-cn.md")
    )

    changelog {
        group = "gitlog"

        toRef = "main"
        preRelease = "SNAPSHOT"

        issueUrlTemplate = "https://${Constants.gitUrl}/${Constants.gitProjectUrl}/issues/:issueId"
        commitUrlTemplate = "https://${Constants.gitUrl}/${Constants.gitProjectUrl}/commit/:commitId"
        mentionUrlTemplate = "https://${Constants.gitUrl}/:username"
        // jsonFile = file("${rootProject.rootDir}/CHANGELOG.json")
        fileSets = gitlogFileSets
        commitLocales = gitlogLocaleProfiles

        outputs.upToDateWhen { false }
    }

    derive {
        group = "gitlog"

        toRef = "main"
        derivedVersionMark = "NEXT_VERSION:=="
        preRelease = "SNAPSHOT"
        commitLocales = gitlogLocaleProfiles

        outputs.upToDateWhen { false }
    }

    register("bumpVersion") {
        group = "gitlog"

        dependsOn(":changelog")

        doLast {
            var newVersion = rootProject.findProperty("newVersion") as? String

            if (newVersion.isNullOrEmpty()) {
                // ^## ([\d\.]+(-SNAPSHOT)?) \(.+\)$
                val changelogContents = file("${rootProject.rootDir}/CHANGELOG.md").readText()
                val versionRegex = Regex("^## ([\\d\\.]+(-SNAPSHOT)?) \\(.+\\)\$", setOf(RegexOption.MULTILINE))
                val changelogVersion = versionRegex.find(changelogContents)?.groupValues?.get(1)

                changelogVersion?.let { newVersion = it }

                logger.warn("changelogVersion: {}", changelogVersion)
                logger.warn("newVersion: {}", newVersion)
            }

            newVersion?.let {
                logger.info("Set Project to new Version $it")

                val contents = buildFile.readText()
                    .replaceFirst("const val projectVersion = \"$version\"", "const val projectVersion = \"$it\"")

                buildFile.writeText(contents)
            }
        }
    }
}
