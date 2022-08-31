package com.bakdata.data2day;


import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.kafka.KafkaStreamsApplication;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import java.util.Map;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

public final class Utils {

    private Utils() {
    }

    public static <T> void extractCorporateInformation(final KStream<String, String> input,
        final String corporateTopic,
        final ValueMapper<? super CorporatePojo, T> valueMapper) {

        final JsonExtractor jsonExtractor = new JsonExtractor();
        input.mapValues(jsonExtractor::extractCorporate)
            .selectKey((key, value) -> value.getId())
            .mapValues(valueMapper)
            .to(corporateTopic);
    }

    public static <T> void extractPersonInformation(
        final KStream<String, String> input,
        final String personTopic,
        final ValueMapper<? super PersonPojo, T> valueMapper) {

        final JsonExtractor jsonExtractor = new JsonExtractor();
        input.mapValues(jsonExtractor::extractPerson)
            .flatMapValues((value) -> value)
            .selectKey((key, value) -> value.getId())
            .mapValues(valueMapper)
            .to(personTopic);
    }

    public static Map<String, String> getSerdeConfig(final KafkaStreamsApplication streamsApplication) {
        return Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
            streamsApplication.getSchemaRegistryUrl());
    }
}
