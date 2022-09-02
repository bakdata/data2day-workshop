plugins {
    java
    idea
    id("com.google.protobuf") version "0.8.19"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.3.0"
    id("io.freefair.lombok") version "6.5.1"
}

group = "com.bakdata.data2day"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    val confluentVersion: String by project
    val protobufVersion: String by project
    val junitVersion: String by project

    implementation(group = "com.bakdata.kafka", name = "streams-bootstrap", version = "2.3.0")
    implementation(group = "com.google.protobuf", name = "protobuf-java", version = protobufVersion)
    implementation(group = "io.confluent", name = "kafka-streams-avro-serde", version = confluentVersion)
    implementation(group = "io.confluent", name = "kafka-streams-protobuf-serde", version = confluentVersion)
    implementation(group = "org.jsoup", name = "jsoup", version = "1.15.2")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.13.3")
    implementation(group = "com.google.protobuf", name = "protobuf-java-util", version = protobufVersion)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "com.google.guava", name = "guava", version = "31.1-jre")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.23.1")
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "fluent-kafka-streams-tests-junit5",
        version = "2.7.0"
    )
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "schema-registry-mock",
        version = "2.7.0"
    )

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
