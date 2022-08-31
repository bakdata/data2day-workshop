package com.bakdata.data2day;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.kafka.KafkaStreamsApplication;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import java.util.Properties;
import lombok.Setter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

@Setter
public class ProtoInformationExtractor extends KafkaStreamsApplication {

    public static final String SCHEMA_NAME = "protobuf";

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
            builder.stream(this.getInputTopics(), Consumed.with(Serdes.String(), Serdes.String()));

        final String corporateTopic = this.getExtraOutputTopics().get(String.format("%s-corporate", SCHEMA_NAME));

        Utils.extractCorporateInformation(input, corporateTopic, CorporatePojo::toProto);

        final String personTopic = this.getExtraOutputTopics().get(String.format("%s-person", SCHEMA_NAME));

        Utils.extractPersonInformation(input, personTopic, PersonPojo::toProto);
    }

    @Override
    public String getUniqueAppId() {
        return "proto-corporate-information-extractor";
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaProtobufSerde.class);

        return kafkaProperties;
    }

    public static void main(final String... args) {
        startApplication(new ProtoInformationExtractor(), args);
    }
}
