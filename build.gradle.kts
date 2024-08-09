import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "is.root"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jogamp.org/deployment/maven")
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains.compose.web:web-core:1.0.0")
}

kotlin {
    sourceSets {
        main {
            dependencies {
                // use api since the desktop app need to access the Cef to initialize it.
                api("io.github.kevinnzou:compose-webview-multiplatform:1.9.20")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.web:web-core:1.0.0") // Web 의존성 추가
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CrashlyticsReportWriter"
            packageVersion = "1.0.0"
        }
    }

    compose.web {
        application {
            // Web 애플리케이션의 진입점을 설정
            mainClass = "MainKt" // Web 진입점으로 변경
        }
    }
}