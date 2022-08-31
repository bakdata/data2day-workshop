plugins {
    java
    idea
    id("com.google.protobuf") version "0.8.19"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.3.0"
}

group = "com.bakdata.data2day"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    implementation(group = "com.bakdata.kafka", name = "streams-bootstrap", version = "2.2.0")
    implementation(group = "com.google.protobuf", name = "protobuf-java", version = "3.21.5")
    implementation(group = "io.confluent", name = "kafka-streams-avro-serde", version = "7.1.1")
    implementation(group = "io.confluent", name = "kafka-streams-protobuf-serde", version = "7.1.1")
    implementation(group = "org.jsoup", name = "jsoup", version = "1.15.2")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.13.3")

    runtimeOnly(group = "com.google.protobuf", name = "protobuf-java-util", version = "3.21.5")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.24")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.24")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.9.0")
    testImplementation(group = "com.google.guava", name = "guava", version = "31.1-jre")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.9.0")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.23.1")
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "fluent-kafka-streams-tests-junit5",
        version = "2.6.0"
    )
    testImplementation(
        group = "com.bakdata.fluent-kafka-streams-tests",
        name = "schema-registry-mock",
        version = "2.6.0"
    )

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
