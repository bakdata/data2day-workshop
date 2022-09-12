package com.bakdata.data2day;

import static org.assertj.core.api.Assertions.assertThat;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.fluent_kafka_streams_tests.TestOutput;
import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;
import com.bakdata.rb.proto.corporate.v1.ProtoCorporate;
import com.bakdata.rb.proto.person.v1.ProtoPerson;
import com.bakdata.schemaregistrymock.SchemaRegistryMock;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.schemaregistry.protobuf.ProtobufSchemaProvider;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class ProtoInformationExtractorTest {

    public static final String INPUT = "announcement-json";
    private final ProtoInformationExtractor app = createExtractionApp();

    @RegisterExtension
    final TestTopologyExtension<Object, Object> testTopology =
        new TestTopologyExtension<>(this.app::createTopology, this.app.getKafkaProperties())
            .withSchemaRegistryMock(new SchemaRegistryMock(List.of(new ProtobufSchemaProvider())));


    @AfterEach
    void tearDown() {
        this.app.close();
    }

    private static ProtoInformationExtractor createExtractionApp() {
        final ProtoInformationExtractor app = new ProtoInformationExtractor();
        app.setInputTopics(List.of(INPUT));
        app.setExtraOutputTopics(
            Map.of("corporate", "protobuf-corporate", "person", "protobuf-person"));
        return app;
    }

    @Test
    void shouldExtractCorporateInProto() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        this.testTopology.input()
            .withSerde(Serdes.String(), Serdes.String())
            .add("1", fixture);

        final JsonExtractor jsonExtractor = new JsonExtractor(false);
        final ProtoCorporate corporate = jsonExtractor.extractCorporate(fixture).get().toProto();

        final TestOutput<Object, Object> producerRecords =
            this.testTopology.streamOutput(this.app.getOutputTopic("corporate"));

        assertThat(producerRecords.readOneRecord())
            .satisfies(record -> {
                assertThat(record).extracting(ProducerRecord::key).isEqualTo(corporate.getId());
                assertThat(record)
                    .extracting(ProducerRecord::value, InstanceOfAssertFactories.type(DynamicMessage.class))
                    .satisfies(value -> assertThat(ProtoCorporate.parseFrom(value.toByteArray())).isEqualTo(corporate));
            });
    }

    @Test
    void shouldExtractPersonInProto() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);
        final JsonExtractor jsonExtractor = new JsonExtractor(false);

        final List<ProtoPerson> person = jsonExtractor.extractPerson(fixture)
            .stream().map(PersonPojo::toProto).collect(Collectors.toList());
        final Serde<ProtoPerson> valueSerde = new KafkaProtobufSerde<>(ProtoPerson.class);

        valueSerde.configure(Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
            this.testTopology.getSchemaRegistryUrl()), false);

        this.testTopology.input()
            .withSerde(Serdes.String(), Serdes.String())
            .add("1", fixture);

        this.testTopology.streamOutput(this.app.getOutputTopic("person"))
            .withSerde(Serdes.String(), valueSerde)
            .expectNextRecord()
            .hasKey(person.get(0).getId())
            .hasValue(person.get(0))
            .expectNextRecord()
            .hasKey(person.get(1).getId())
            .hasValue(person.get(1))
            .expectNoMoreRecord();
    }

    @Test
    void shouldNotExtractWithCorporateDataInAvro() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("exception_test.json"), Charsets.UTF_8);

        this.testTopology.input()
            .withSerde(Serdes.String(), Serdes.String())
            .add("1", fixture);

        this.testTopology.streamOutput(this.app.getOutputTopic("corporate")).expectNoMoreRecord();
    }
}
