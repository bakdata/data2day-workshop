# Kafka Streams

Setup:

1. Generate Avro Java with the gradle task generateAvroJava: `gradle generateAvroJava`
2. Generate Proto:
    - Install `protoc`, see: https://grpc.io/docs/protoc-installation/
    - Run gradle task generateProto: `gradle generateProto`
3. Run the Streams Apps for Avro and Protobuf and set the following program arguments:
    - Avro `com.bakdata.data2day.AvroInformationExtractor`: `--brokers=localhost:29092 --schema-registry-url=http://localhost:8081 --input-topics=announcements --extra-output-topics=corporate=proto-corporate,person=proto-person`
    - Proto `com.bakdata.data2day.ProtoInformationExtractor`: `--brokers=localhost:29092 --schema-registry-url=http://localhost:8081 --input-topics=announcements --extra-output-topics=corporate=avro-corporate,person=avro-person`
