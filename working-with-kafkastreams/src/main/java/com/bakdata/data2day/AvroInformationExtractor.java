package com.bakdata.data2day;

import static com.bakdata.data2day.Utils.extractCorporateInformation;
import static com.bakdata.data2day.Utils.extractPersonInformation;

import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.kafka.KafkaStreamsApplication;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

public class AvroInformationExtractor extends KafkaStreamsApplication {

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
            builder.stream(this.getInputTopics(), Consumed.with(Serdes.String(), Serdes.String()));

        final String corporateTopic = this.getExtraOutputTopics().get(String.format("%s-corporate", "avro"));

        extractCorporateInformation(input, corporateTopic, CorporatePojo::toAvro);

        final String personTopic = this.getExtraOutputTopics().get(String.format("%s-person", "avro"));

        extractPersonInformation(input, personTopic, PersonPojo::toAvro);
    }

    @Override
    public String getUniqueAppId() {
        return "avro-corporate-information-extractor";
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        return kafkaProperties;
    }

    public static void main(final String... args) {
        startApplication(new ProtoInformationExtractor(), args);
    }
}
