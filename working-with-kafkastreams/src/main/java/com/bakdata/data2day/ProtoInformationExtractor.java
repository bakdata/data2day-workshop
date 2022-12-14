package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.kafka.KafkaStreamsApplication;
import com.bakdata.rb.proto.corporate.v1.ProtoCorporate;
import com.bakdata.rb.proto.person.v1.ProtoPerson;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import java.util.Properties;
import lombok.Setter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import picocli.CommandLine;

/**
 * Kafka streams application for extracting person and corporate information in Protobuf.
 */
@Setter
public class ProtoInformationExtractor extends KafkaStreamsApplication {

    @CommandLine.Option(names = "--throw-exception",
            description = "If the streams app should only log errors or throw an exception.", arity = "0..1")
    private boolean shouldThrowException;

    public static void main(final String... args) {
        startApplication(new ProtoInformationExtractor(), args);
    }

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
                builder.stream(this.getInputTopics(), Consumed.with(null, Serdes.String()));

        final JsonExtractor jsonExtractor = new JsonExtractor(this.shouldThrowException);

        final KStream<String, ProtoCorporate> corporates = null; //TODO extract corporates here
        corporates.to(this.getCorporateTopic());

        final KStream<String, ProtoPerson> persons = null; //TODO extract persons here
        persons.to(this.getPersonTopic());
    }

    @Override
    public String getUniqueAppId() {
        return String.format("proto-corporate-information-extractor-%s-%s", this.getCorporateTopic(),
                this.getPersonTopic());
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaProtobufSerde.class);

        return kafkaProperties;
    }

    String getCorporateTopic() {
        return this.getOutputTopic("corporate");
    }

    String getPersonTopic() {
        return this.getOutputTopic("person");
    }
}
