plugins {
	java
}

allprojects {
	repositories {
		mavenLocal()
		mavenCentral()
		google()
	}
}

subprojects {
	apply(plugin = "java")
}