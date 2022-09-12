# Kafka Streams

1. Make sure Java 11 is installed
2. Decide whether you want to use Avro or Protobuf for your Shemas:
    - Avro:
        1. Generate Avro Java with the gradle task generateAvroJava: `./gradlew generateAvroJava`
        2. To run use either the run configuration or this run this
           class `com.bakdata.data2day.AvroInformationExtractor` with the following
           arguments `--brokers=localhost:29092 --schema-registry-url=http://localhost:8081 --input-topics=announcements --extra-output-topics=corporate=avro-corporate --extra-output-topics=person=avro-person`
    - Protobuf:
        1. Install `protoc`, see: https://grpc.io/docs/protoc-installation/
        2. Run gradle task generateProto: `./gradlew generateProto`
        3. To run use either the run configuration or this run this
           class  `com.bakdata.data2day.ProtoInformationExtractor` with the following
           arguments `--brokers=localhost:29092 --schema-registry-url=http://localhost:8081 --input-topics=announcements --extra-output-topics=corporate=proto-corporate --extra-output-topics=person=proto-person`
