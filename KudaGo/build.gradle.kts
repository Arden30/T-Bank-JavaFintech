plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "arden.java"
version = "0.0.1-SNAPSHOT"

val aspectVersion: String by project
val wiremockTestcontainersVersion: String by project
val wiremockVersion: String by project
val testcontainersVersion: String by project

jacoco {
	toolVersion = "0.8.12"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

dependencies {
	implementation(project(":starter"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("org.aspectj:aspectjweaver:${aspectVersion}")
	implementation("org.aspectj:aspectjrt:${aspectVersion}")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.wiremock:wiremock-standalone:${wiremockVersion}")
	testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:${wiremockTestcontainersVersion}")
	testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"arden/java/kudago/dto/**",
					"arden/java/kudago/exception/**"
				)
			}
		})
	)
}
