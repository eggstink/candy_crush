// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
//        classpath(libs.google.services) // Use the latest version
        classpath("com.google.gms:google-services:4.4.1")
    }
}