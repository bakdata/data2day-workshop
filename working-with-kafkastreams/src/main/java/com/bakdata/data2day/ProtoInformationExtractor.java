package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.kafka.KafkaStreamsApplication;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import java.util.Optional;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import picocli.CommandLine;

/**
 * Kafka streams application for extracting person and corporate information in Protobuf.
 */
public class ProtoInformationExtractor extends KafkaStreamsApplication {

    @CommandLine.Option(names = "--throw-exception",
        description = "If the streams app should only log errors or throw an exception.")
    private boolean shouldThrowException = false;

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
            builder.stream(this.getInputTopics(), Consumed.with(null, Serdes.String()));

        final JsonExtractor jsonExtractor = new JsonExtractor(this.shouldThrowException);

        final String corporateTopic = this.getOutputTopic("corporate");

        input.mapValues(jsonExtractor::extractCorporate)
            .filter(((key, value) -> value.isPresent()))
            .mapValues(Optional::get)
            .selectKey((key, value) -> value.getId())
            .mapValues(CorporatePojo::toProto)
            .to(corporateTopic);

        final String personTopic = this.getOutputTopic("person");

        input.flatMapValues(jsonExtractor::extractPerson)
            .selectKey((key, value) -> value.getId())
            .mapValues(PersonPojo::toProto)
            .to(personTopic);
    }

    @Override
    public String getUniqueAppId() {
        return String.format("proto-corporate-information-extractor-%s-%s", this.getOutputTopic("corporate"),
                this.getOutputTopic("person"));
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaProtobufSerde.class);

        return kafkaProperties;
    }

    public static void main(final String... args) {
        startApplication(new ProtoInformationExtractor(), args);
    }
}
