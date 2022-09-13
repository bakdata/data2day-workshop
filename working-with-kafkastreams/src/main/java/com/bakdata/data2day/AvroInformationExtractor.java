package com.bakdata.data2day;

import com.bakdata.data2day.extractor.JsonExtractor;
import com.bakdata.data2day.model.CorporatePojo;
import com.bakdata.data2day.model.PersonPojo;
import com.bakdata.kafka.AvroDeadLetterConverter;
import com.bakdata.kafka.ErrorCapturingFlatValueMapper;
import com.bakdata.kafka.KafkaStreamsApplication;
import com.bakdata.kafka.ProcessedValue;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import picocli.CommandLine;

/**
 * Kafka streams application for extracting person and corporate information in Avro.
 */
public class AvroInformationExtractor extends KafkaStreamsApplication {

    @CommandLine.Option(names = "--throw-exception",
        description = "If the streams app should only log errors or throw an exception.", arity = "0..1")
    private boolean shouldThrowException = false;

    @Override
    public void buildTopology(final StreamsBuilder builder) {
        final KStream<String, String> input =
            builder.stream(this.getInputTopics(), Consumed.with(null, Serdes.String()));
        final JsonExtractor jsonExtractor = new JsonExtractor(this.shouldThrowException);

        final KStream<String, ProcessedValue<String, CorporatePojo>> mapped =
                input.flatMapValues(ErrorCapturingFlatValueMapper.captureErrors(
                        json -> jsonExtractor.extractCorporate(json).stream().collect(Collectors.toList())));
        mapped.flatMapValues(ProcessedValue::getValues)
            .selectKey((key, value) -> value.getId())
            .mapValues(CorporatePojo::toAvro)
            .to(this.getOutputTopic("corporate"));
        mapped.flatMapValues(ProcessedValue::getErrors)
            .transformValues(AvroDeadLetterConverter.asTransformer("Error parsing corporate"))
            .to(this.getErrorTopic());

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
