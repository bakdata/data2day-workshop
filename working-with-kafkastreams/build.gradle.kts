plugins {
    java
    idea
    id("com.google.protobuf") version "0.8.19"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.3.0"
    id("io.freefair.lombok") version "6.5.1"
    id("com.google.cloud.tools.jib") version "3.1.4"
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
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.13.3")
    implementation(group = "com.google.protobuf", name = "protobuf-java-util", version = protobufVersion)
    implementation(group = "com.bakdata.kafka", name = "error-handling-avro", version = "1.3.0")
    implementation(group = "info.picocli", name = "picocli", version = "4.6.3")

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

jib {
    container {
        mainClass = "com.bakdata.data2day.AvroInformationExtractor"
    }
}

avro {
    setGettersReturnOptional(true)
    setOptionalGettersForNullableFieldsOnly(true)
    setFieldVisibility("PRIVATE")
}


val brokersArg = "--brokers=localhost:29092"
val schemaRegistryArg = "--schema-registry-url=http://localhost:8081"
val inputTopicArg = "--input-topics=announcements"

tasks.register<JavaExec>("runAvroInformationExtractor") {
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set("com.bakdata.data2day.AvroInformationExtractor")
    args(listOf(
            brokersArg,
            schemaRegistryArg,
            inputTopicArg,
            "--extra-output-topics=corporate=avro-corporate",
            "--extra-output-topics=person=avro-person"
    ))
}

tasks.register<JavaExec>("runProtoInformationExtractor") {
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set("com.bakdata.data2day.ProtoInformationExtractor")
    args(listOf(
            brokersArg,
            schemaRegistryArg,
            inputTopicArg,
            "--extra-output-topics=corporate=avro-corporate",
            "--extra-output-topics=person=avro-person"
    ))
}
