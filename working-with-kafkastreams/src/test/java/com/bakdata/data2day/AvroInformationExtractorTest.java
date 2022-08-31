package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;
import com.bakdata.rb.avro.corporate.v1.AvroCorporate;
import com.bakdata.rb.avro.person.v1.AvroPerson;
import com.bakdata.schemaregistrymock.SchemaRegistryMock;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serdes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class AvroInformationExtractorTest {
    public static final String INPUT = "announcement-html";
    private final AvroInformationExtractor app = createExtractionApp();
    @RegisterExtension
    final TestTopologyExtension<Object, Object> testTopology = new TestTopologyExtension<>(
        (props) -> {
            this.app.setSchemaRegistryUrl(props.getProperty(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG));
            return this.app.createTopology();
        }, this.app.getKafkaProperties()
    ).withSchemaRegistryMock(new SchemaRegistryMock(List.of(new AvroSchemaProvider())));

    @AfterEach
    void tearDown() {
        this.app.close();
    }

    private static AvroInformationExtractor createExtractionApp() {
        final AvroInformationExtractor app = new AvroInformationExtractor();
        app.setInputTopics(List.of(INPUT));
        app.setExtraOutputTopics(Map.of("avro-corporate", "avro-corporate", "avro-person", "avro-person"));
        return app;
    }

    @Test
    void shouldExtractCorporateInProto() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        this.testTopology.input()
            .withSerde(Serdes.String(), Serdes.String())
            .add("1", fixture);

        final JsonExtractor jsonExtractor = new JsonExtractor();
        final AvroCorporate corporate = jsonExtractor.extractCorporate(fixture).toAvro();

        this.testTopology.streamOutput(this.app.getOutputTopic("avro-corporate"))
            .expectNextRecord()
            .hasKey(corporate.getId())
            .hasValue(corporate)
            .expectNoMoreRecord();
    }

    @Test
    void shouldExtractPersonInProto() throws IOException {
        final String fixture = Resources.toString(Resources.getResource("test.json"), Charsets.UTF_8);

        final JsonExtractor jsonExtractor = new JsonExtractor();
        final List<AvroPerson> person = jsonExtractor.extractPerson(fixture)
            .stream().map(PersonPojo::toAvro).collect(Collectors.toList());

        this.testTopology.input()
            .withSerde(Serdes.String(), Serdes.String())
            .add("1", fixture);

        this.testTopology.streamOutput(this.app.getOutputTopic("avro-person"))
            .expectNextRecord()
            .hasKey(person.get(0).getId())
            .hasValue(person.get(0))
            .expectNextRecord()
            .hasKey(person.get(1).getId())
            .hasValue(person.get(1));
    }
}
