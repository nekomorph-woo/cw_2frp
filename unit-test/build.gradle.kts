plugins {
    kotlin("jvm") version "2.1.20"
}

group = "cw.chaos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// 只包含纯逻辑模块的源代码（不依赖 IntelliJ API）
sourceSets {
    named("main") {
        kotlin {
            srcDir("../src/main/kotlin/cw/chaos/cw2_frp")
            // 排除需要 IntelliJ 依赖的文件
            exclude("MyToolWindow.kt")
            exclude("MyMessageBundle.kt")
            exclude("FRPAction.kt")
            exclude("ClipboardService.kt")
            exclude("PsiContextDetector.kt")
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
