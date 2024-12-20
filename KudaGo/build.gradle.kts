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
val googleGuavaVersion: String by project
val swaggerVersion: String by project

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
	maven {
		url = uri("https://repo.kaczmarzyk.net")
	}
}

dependencies {
	implementation(project(":starter"))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.data:spring-data-commons")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.liquibase:liquibase-core")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("net.kaczmarzyk:specification-arg-resolver:3.1.0")
	implementation("org.aspectj:aspectjweaver:${aspectVersion}")
	implementation("org.aspectj:aspectjrt:${aspectVersion}")
	implementation("com.google.guava:guava:${googleGuavaVersion}")
	implementation ("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swaggerVersion}")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	testImplementation("org.wiremock:wiremock-standalone:${wiremockVersion}")
	testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:${wiremockTestcontainersVersion}")
	testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
	testImplementation("org.testcontainers:postgresql")
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
