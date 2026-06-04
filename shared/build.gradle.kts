import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

val publishVersion = System.getenv("BITRISE_GIT_TAG")
    ?: localProperties["version"] as String?
    ?: "0.1.0-SNAPSHOT"

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("maven-publish")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Auth"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "dev.finio.auth"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

publishing{
    publications{
        withType<MavenPublication> {
            groupId = "dev.finio"
            version = publishVersion
            artifactId = when(name){
                "android" -> "finio-auth-android"
                "iosArm64" -> "finio-auth-iosarm64"
                "iosSimulatorArm64" -> "finio-auth-iossimulatorarm64"
                "kotlinMultiplatform" -> "finio-auth-kmp"
                else -> "finio-auth-$name"
            }

            pom{
                name.set("Finio Auth")
                description.set("Finio KMP Module for authentication")
                url.set("https://github.com/dgbarreto/finio-auth")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licences/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("dgbarreto")
                        name.set("Danilo Barreto")
                        email.set("dgbarreto@gmail.com")
                    }
                }
            }
        }
    }

    repositories{
        maven{
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dgbarreto/finio-auth")
            credentials {
                username = localProperties["github.actor"] as String?
                    ?: System.getenv("GITHUB_ACTOR")
                password = localProperties["github.token"] as String?
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

