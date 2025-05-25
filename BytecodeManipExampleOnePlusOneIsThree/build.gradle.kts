plugins {
	`java-library`
}

dependencies {
	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.platform.launcher)
}

group = "com.etk2000.bcm"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	// the below is needed for `testMainWithReflection`
	jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}