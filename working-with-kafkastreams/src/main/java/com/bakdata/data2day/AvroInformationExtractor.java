package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
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
            builder.stream(this.getInputTopics(), Consumed.with(null, Serdes.String()));
        final JsonExtractor jsonExtractor = new JsonExtractor();

        final String corporateTopic = this.getOutputTopic("corporate");
        input.mapValues(jsonExtractor::extractCorporate)
            .selectKey((key, value) -> value.getId())
            .mapValues(CorporatePojo::toAvro)
            .to(corporateTopic);

        final String personTopic = this.getOutputTopic("person");
        input.flatMapValues(jsonExtractor::extractPerson)
            .selectKey((key, value) -> value.getId())
            .mapValues(PersonPojo::toAvro)
            .to(personTopic);
    }

    @Override
    public String getUniqueAppId() {
        return String.format("avro-corporate-information-extractor-%s-%s", this.getOutputTopic("corporate"),
                this.getOutputTopic("person"));
    }

    @Override
    protected Properties createKafkaProperties() {
        final Properties kafkaProperties = super.createKafkaProperties();
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        return kafkaProperties;
    }

    public static void main(final String... args) {
        startApplication(new AvroInformationExtractor(), args);
    }
}
