import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.3.0"
}

group = "is.root"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("androidx.compose.material3:material3:1.2.1") // Material3의 최신 안정화 버전 사용
    implementation("androidx.compose.ui:ui") // WebView 의존성 추가
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
}
