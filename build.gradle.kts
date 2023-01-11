plugins {
    kotlin("jvm")
    application
}

group = "org.jire.easyffm"
version = "0.1.0-SNAPSHOT"

dependencies {
    implementation("org.javassist:javassist:3.29.2-GA")
}

application {
    mainClass.set("org.jire.easyffm.Test")
    applicationDefaultJvmArgs += arrayOf(
        "--enable-preview",
        "--enable-native-access=ALL-UNNAMED",

        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

kotlin {
    jvmToolchain(19)
}

tasks.compileKotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.compileJava {
    options.compilerArgs.add("--enable-preview")
}
