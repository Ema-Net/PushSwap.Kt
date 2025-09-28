import groovyjarjarantlr.build.ANTLR.jarName
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.1.10"
	application
}

group = "me.emaryllis"
version = "1.0"

repositories {
	mavenCentral()
}

dependencies {
	// Junit 5 (Included with Kotlin)
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter:junit-jupiter-params:${project.property("junit_version")}")

	// Junit 5 Suite (Groups tests together)
	testImplementation("org.junit.platform:junit-platform-suite-api:${project.property("junit_version")}")
	testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:${project.property("junit_version")}")
}
tasks {
	named<JavaCompile>("compileJava") { enabled = false }
	named<JavaCompile>("compileTestJava") { enabled = false }

	build {
		outputs.cacheIf { true }
		inputs.files(fileTree(sourceSets.main.get().allSource))
	}

	test {
		outputs.cacheIf { false }
		exclude("**/*Suite*")
		useJUnitPlatform()
		testLogging {
			events("skipped", "failed", "standardOut", "standardError")
			exceptionFormat = TestExceptionFormat.FULL
			showStandardStreams = true
		}
		maxParallelForks = Runtime.getRuntime().availableProcessors()
	}
}
kotlin {
	jvmToolchain(21)
}

application {
	mainClass.set("me.emaryllis.MainKt")
}