dependencies {
	testImplementation(project(":BytecodeManipExampleOnePlusOneIsThree"))
	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.platform.launcher)
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

// slot a task in during the compilation phase so it runs for normal compiles
tasks.register(
	"bytecodeManipulation",
	com.etk2000.bcm.oneplusone.DependencyPatcherTask::class
) {
	dependsOn("compileJava")
	classpathConfiguration = configurations["compileClasspath"]
}
tasks["classes"].dependsOn("bytecodeManipulation")

// slot a task in during the test compilation phase so it runs for tests
tasks.register(
	"bytecodeManipulationTesting",
	com.etk2000.bcm.oneplusone.DependencyPatcherTask::class
) {
	dependsOn("classes")
	classpathConfiguration = configurations["testCompileClasspath"]
}
tasks["compileTestJava"].dependsOn("bytecodeManipulationTesting")