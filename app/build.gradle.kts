plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jspecify:jspecify:1.0.0")

    implementation("org.apache.parquet:parquet-avro:1.15.0")
    implementation("org.apache.hadoop:hadoop-common:3.4.1")
    implementation("org.apache.parquet:parquet-hadoop:1.13.0")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:3.3.5")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "nytaxi.Benchmark"
}

tasks.named<JavaExec>("run") {
    workingDir = file("..")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
