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

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}

application {
	mainClass.set("me.emaryllis.MainKt")
}