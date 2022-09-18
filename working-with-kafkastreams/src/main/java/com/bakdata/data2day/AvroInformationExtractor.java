package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.kafka.KafkaStreamsApplication;
import com.bakdata.rb.avro.corporate.v1.AvroCorporate;
import com.bakdata.rb.avro.person.v1.AvroPerson;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;
import lombok.Setter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import picocli.CommandLine;

/**
 * Kafka streams application for extracting person and corporate information in Avro.
 */
@Setter
public class AvroInformationExtractor extends KafkaStreamsApplication {

    @CommandLine.Option(names = "--throw-exception",
            description = "If the streams app should only log errors or throw an exception.", arity = "0..1")
    private boolean shouldThrowException;

    public static void main(final String... args) {
        startApplication(new AvroInformationExtractor(), args);
    }

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
                builder.stream(this.getInputTopics(), Consumed.with(null, Serdes.String()));
        final JsonExtractor jsonExtractor = new JsonExtractor(this.shouldThrowException);

        final KStream<String, AvroCorporate> corporates = null; //TODO extract corporates here
        corporates.to(this.getCorporateTopic());

        final KStream<String, AvroPerson> persons = null; //TODO extract persons here
        persons.to(this.getPersonTopic());
    }

    @Override
    public String getUniqueAppId() {
        return String.format("avro-corporate-information-extractor-%s-%s", this.getCorporateTopic(),
                this.getPersonTopic());
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        return kafkaProperties;
    }

    String getPersonTopic() {
        return this.getOutputTopic("person");
    }

    String getCorporateTopic() {
        return this.getOutputTopic("corporate");
    }
}
