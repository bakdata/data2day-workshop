# Kafka Streams

1. Make sure Java 11 is installed and `protoc`, see: https://grpc.io/docs/protoc-installation/
2. Decide whether you want to use Avro or Protobuf for your Schemas:
    - Avro:
        1. Generate Avro Java with the Gradle task generateAvroJava: `./gradlew generateAvroJava`
        2. To run use either the run configuration in IntelliJ or run the Gradle task `runAvroInformationExtractor`
    - Protobuf:
        1. Run gradle task generateProto: `./gradlew generateProto`
        2. To run use either the run configuration in IntelliJ or run the Gradle task `runProtoInformationExtractor`
